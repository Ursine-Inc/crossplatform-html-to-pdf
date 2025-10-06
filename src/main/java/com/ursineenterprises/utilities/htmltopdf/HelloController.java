package com.ursineenterprises.utilities.htmltopdf;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.application.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
    public VBox zipView;
    public VBox dirView;
    public Label welcomeText1;
    public Label pathToZipLabel;
    public Button fromZip;
    public Button fromFolder;
    public HBox toggleBox;

    @FXML
    private Label folderHeading;
    @FXML
    private Label zipHeading;

    @FXML
    private Label pathToDirLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private Label filesCountLabel;

    private static final Logger logger = LoggerFactory.getLogger((HelloController.class.getName()));

    @FXML
    public void initialize() {
        folderHeading.setText("Find the folder on your computer containing the HTML files.");
     // zipHeading.setText("Find the zip file on your computer containing the ZIP file.");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Point to HTML Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
    }

    @FXML
    private void onBrowseButtonClicked(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select HTML Files Directory");
        File directory = directoryChooser.showDialog(((Node)event.getSource()).getScene().getWindow());
        if (directory != null) {
            pathToDirLabel.setText(directory.getAbsolutePath());
            System.out.println("Pointing application to "+directory.getAbsolutePath());
        }

        assert directory != null;
        pathToDirLabel.setText(directory.getAbsolutePath());
    }


    public void onSelectType(ActionEvent event) {
        switch (((Button) event.getSource()).getId()) {
            case "fromZip": {
                dirView.setVisible(! dirView.isVisible());
                zipView.setVisible(true);
            } break;
            case "fromFolder": {
                zipView.setVisible(! zipView.isVisible());
                dirView.setVisible(true);
            } break;
        }
    }

    public void onBrowseDirectoryPath(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Process");
        File selectedDir = directoryChooser.showDialog(((Node)event.getSource()).getScene().getWindow());

        if (selectedDir != null) {
            logger.info("Processing directory: " + selectedDir.getAbsolutePath());
            pathToDirLabel.setText(selectedDir.getAbsolutePath());
            showProgressUI();
            new Thread(() -> processDirectoryWithProgress(selectedDir)).start();
        }
    }

    private void showProgressUI() {
        progressBar.setVisible(true);
        progressBar.setProgress(0);
        progressLabel.setVisible(true);
        progressLabel.setText("Scanning files...");
    }

    private void processDirectoryWithProgress(File dir) {
        List<File> htmlFiles = collectHtmlFiles(dir);

        int totalFiles = htmlFiles.size();
        logger.info("Found {} HTML files to convert", totalFiles);

        if (totalFiles == 0) {
            Platform.runLater(() -> {
                progressLabel.setText("No HTML files found in directory.");
                progressBar.setVisible(false);
            });
            return;
        }

        // Create output directory
        File outputDir = createOutputDirectory(dir);
        if (outputDir == null) {
            Platform.runLater(() -> {
                progressLabel.setText("Error: Could not create output directory.");
                progressBar.setVisible(false);
            });
            return;
        }

        updateProgress(0, totalFiles);

        for (int i = 0; i < htmlFiles.size(); i++) {
            generatePdfFromHtml(htmlFiles.get(i), dir, outputDir);
            updateProgress(i + 1, totalFiles);
        }

        showCompletionMessage(totalFiles, outputDir);
    }

    private File createOutputDirectory(File sourceDir) {
        String outputDirName = sourceDir.getName() + "_pdf";
        File outputDir = new File(sourceDir.getParentFile(), outputDirName);

        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                logger.error("Failed to create output directory: {}", outputDir.getAbsolutePath());
                return null;
            }
        }

        logger.info("Output directory: {}", outputDir.getAbsolutePath());
        return outputDir;
    }

    private List<File> collectHtmlFiles(File dir) {
        List<File> htmlFiles = new ArrayList<>();
        collectHtmlFilesRecursive(dir, htmlFiles);

        int totalFiles = htmlFiles.size();
        if (filesCountLabel != null) {
            Platform.runLater(() ->
                filesCountLabel.setText("Total HTML files found: " + totalFiles)
            );
        }

        return htmlFiles;
    }

    private void collectHtmlFilesRecursive(File dir, List<File> htmlFiles) {
        File[] files = dir.listFiles();
        if (files == null) {
            logger.warn("Cannot access directory: {}", dir.getAbsolutePath());
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                collectHtmlFilesRecursive(file, htmlFiles);
            } else if (isHtmlFile(file)) {
                htmlFiles.add(file);
            }
        }
    }

    private boolean isHtmlFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".html") || name.endsWith(".htm");
    }

    private void updateProgress(int processed, int total) {
        double progress = total > 0 ? (double) processed / total : 0;
        Platform.runLater(() -> {
            progressBar.setProgress(progress);
            progressLabel.setText(String.format("Converting %d/%d files...", processed, total));
        });
    }

    private void showCompletionMessage(int totalFiles, File outputDir) {
        Platform.runLater(() -> {
            progressBar.setProgress(1.0);
            progressLabel.setText(String.format("Complete! Converted %d file%s to: %s",
                totalFiles, totalFiles == 1 ? "" : "s", outputDir.getAbsolutePath()));
            scheduleProgressHide();
        });
    }

    private void scheduleProgressHide() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    progressLabel.setVisible(false);
                });
            } catch (InterruptedException e) {
                logger.error("Progress hide interrupted", e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void generatePdfFromHtml(File htmlFile, File rootSourceDir, File rootOutputDir) {
        try {
            logger.info("Converting: {}", htmlFile.getName());

            String htmlContent = Files.readString(htmlFile.toPath());

            // Use jsoup to parse and clean HTML properly
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(htmlContent, htmlFile.toURI().toString());
            doc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
            String cleanHtml = doc.html();

            // Create PDF file with preserved directory structure
            File pdfFile = createPdfFileWithStructure(htmlFile, rootSourceDir, rootOutputDir);

            try (FileOutputStream os = new FileOutputStream(pdfFile)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withHtmlContent(cleanHtml, htmlFile.toURI().toString());
                builder.toStream(os);
                builder.run();
            }

            logger.info("Generated PDF: {}", pdfFile.getAbsolutePath());

        } catch (IOException e) {
            logger.error("Failed to generate PDF for {}: {}", htmlFile.getName(), e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error processing {}: {}", htmlFile.getName(), e.getMessage(), e);
        }
    }

    private File createPdfFileWithStructure(File htmlFile, File rootSourceDir, File rootOutputDir) throws IOException {
        // Get relative path from source root
        String relativePath = rootSourceDir.toPath().relativize(htmlFile.toPath()).toString();

        // Replace file extension
        String pdfRelativePath = relativePath.replaceFirst("\\.[^.]+$", ".pdf");

        // Create target file
        File pdfFile = new File(rootOutputDir, pdfRelativePath);

        // Ensure parent directories exist
        File parentDir = pdfFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
        }

        return pdfFile;
    }
}

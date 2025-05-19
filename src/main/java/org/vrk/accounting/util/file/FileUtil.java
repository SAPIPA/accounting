package org.vrk.accounting.util.file;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Act;
import org.vrk.accounting.domain.Application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public File generateModernizationAct(Act act) {
        return new File("/");
    }

    public File generateFIU10Act(Act act) {
        return new File("/");
    }

    public File generateFMU73Act(Act act) {
        return new File("/");
    }

    public File generateTransferAct(Act act) {
        return new File("/");
    }

    public File generateAct(Act act) throws IOException {

        switch (act.getType()) {
            case MODERNIZATION -> generateModernizationAct(act);
            case FIU10 -> generateFIU10Act(act);
            case FMU73 -> generateFMU73Act(act);
            case TRANSFER -> generateTransferAct(act);
        }
        // Убедимся, что директория существует
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        // Имя файла
        String filename = "act_" + act.getId() + ".docx";
        Path filePath = uploadPath.resolve(filename);

        // Генерация документа
        try (XWPFDocument document = new XWPFDocument()) {
            // заголовок
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
//            titleRun.setText(act.getName());
            titleRun.setFontSize(16);
            titleRun.setFontFamily("Times New Roman");
            titleRun.setBold(true);

            // содержание
            XWPFParagraph content = document.createParagraph();
            content.setAlignment(ParagraphAlignment.BOTH);
            XWPFRun contentRun = content.createRun();
//            contentRun.setText(act.getDescription());
            contentRun.setFontSize(14);
            contentRun.setFontFamily("Times New Roman");

            // Запись в файл
            try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                document.write(out);
            }
        }

        return filePath.toFile();
    }

    public File generateServiceApplication(Application application) {
        return new File("/");
    }

    public File generateWriteOffApplication(Application application) {
        return new File("/");
    }

    public File generateAcquisitionApplication(Application application) {
        return new File("/");
    }

    public File generateAddApplication(Application application) {
        return new File("/");
    }

    public File generateApplication(Application application) throws IOException {
        switch (application.getType()) {
            case ADD -> generateAddApplication(application);
            case SERVICE -> generateServiceApplication(application);
            case WRITE_OFF -> generateWriteOffApplication(application);
            case ACQUISITION -> generateAcquisitionApplication(application);
        }
        // Убедимся, что директория существует
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        // Имя файла
//        String filename = "act_" + act.getId() + ".docx";
        Path filePath = uploadPath.resolve("filename");

        // Генерация документа
        try (XWPFDocument document = new XWPFDocument()) {
            // заголовок
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
//            titleRun.setText(act.getName());
            titleRun.setFontSize(16);
            titleRun.setFontFamily("Times New Roman");
            titleRun.setBold(true);

            // содержание
            XWPFParagraph content = document.createParagraph();
            content.setAlignment(ParagraphAlignment.BOTH);
            XWPFRun contentRun = content.createRun();
//            contentRun.setText(act.getDescription());
            contentRun.setFontSize(14);
            contentRun.setFontFamily("Times New Roman");

            // Запись в файл
            try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                document.write(out);
            }
        }

        return filePath.toFile();
    }
}
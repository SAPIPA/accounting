package org.vrk.accounting.util.file;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.style.BorderStyle;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable.XWPFBorderType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Act;
import org.vrk.accounting.domain.Application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class FileUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Если шаблоны лежат в resources/templates/
    @Value("classpath:templates/FMU73_Template.docx")
    private Resource template;

    private static final DateTimeFormatter RUSSIAN_FORMATTER = DateTimeFormatter.ofPattern("«d» MMMM yyyy 'г.'", new Locale("ru"));
    /**
     * Генерирует акт по модели Act.
     * @param act модель с полями и списками
     * @return готовый .docx в байтах
     */
    public File generateFMU73Act(Act act) throws IOException {

        // 1) Собираем простые поля
        Map<String, Object> model = new HashMap<>();
        model.put("organization",   act.getBody().get("organization"));
        String isoDate = act.getBody().get("date").toString();
        LocalDate date = LocalDate.parse(isoDate);
        String formattedDate = date.format(RUSSIAN_FORMATTER);
        model.put("date", formattedDate);            model.put("writeOffReason", act.getBody().get("writeOffReason"));
        model.put("mainEngineer",   act.getBody().get("mainEngineer"));
        model.put("items", act.getBody().get("items"));
        model.put("commissionMembers", act.getBody().get("commissionMembers"));

        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
        Configure config = Configure.builder().bind("items", policy).bind("commissionMembers", policy).build();

        // 4) Строим стиль границ
        BorderStyle borderStyle = new BorderStyle();
        borderStyle.setColor("A6A6A6");
        borderStyle.setSize(4);
        borderStyle.setType(XWPFBorderType.SINGLE);

        // 7) Путь вывода
        String filename = String.format("FMU73_act_%d.docx", act.getId());
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        File outFile = uploadPath.resolve(filename).toFile();

        // 8) Рендерим
        try (InputStream is = template.getInputStream();
             XWPFTemplate tpl = XWPFTemplate.compile(is, config).render(model);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            tpl.write(fos);
        }

        return outFile;
    }

    public File generateModernizationAct(Act act) {
        return new File("/");
    }

    public File generateFIU10Act(Act act) {
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
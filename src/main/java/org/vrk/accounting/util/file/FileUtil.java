package org.vrk.accounting.util.file;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.style.BorderStyle;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFTable.XWPFBorderType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.dto.ActDTO;
import org.vrk.accounting.domain.dto.ApplicationDTO;

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
@RequiredArgsConstructor
public class FileUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("classpath:templates/FIU10_Template.docx")
    private Resource FIU10Template;
    @Value("classpath:templates/FMU73_Template.docx")
    private Resource FMU73Template;
    @Value("classpath:templates/SERVICE_Template.docx")
    private Resource ServiceTemplate;
    @Value("classpath:templates/TRANSFER_Template.docx")
    private Resource TransferTemplate;
    @Value("classpath:templates/WRITE_OFF_Template.docx")
    private Resource WriteOffTemplate;
    @Value("classpath:templates/ACQUISITION_Template.docx")
    private Resource AcquisitionTemplate;
    @Value("classpath:templates/ADD_Template.docx")
    private Resource AddTemplate;
    @Value("classpath:templates/InventoryList_Template.docx")
    private Resource InventoryListTemplate;

    private static final DateTimeFormatter RUSSIAN_FORMATTER = DateTimeFormatter.ofPattern("«d» MMMM yyyy 'г.'", new Locale("ru"));

    private File generateFIU10Act(ActDTO act) {
        return new File("/");
    }

    /**
     * Генерирует акт по модели Act.
     * @param act модель с полями и списками
     * @return готовый .docx в байтах
     */
    private File generateFMU73Act(ActDTO act) throws IOException {

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
        try (InputStream is = FMU73Template.getInputStream();
             XWPFTemplate tpl = XWPFTemplate.compile(is, config).render(model);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            tpl.write(fos);
        }

        return outFile;
    }

    private File generateServiceAct(ActDTO act) {
        return new File("/");
    }

    private File generateTransferAct(ActDTO act) {
        return new File("/");
    }

    public File generateAct(ActDTO act) throws IOException {
        switch (act.getType()) {
            case FIU10 -> generateFIU10Act(act);
            case FMU73 -> generateFMU73Act(act);
            case SERVICE -> generateServiceAct(act);
            case TRANSFER -> generateTransferAct(act);
            default -> throw new IllegalArgumentException("Unsupported type: " + act.getType());
        }
        return null;
    }


    private File generateWriteOffApplication(ApplicationDTO application) {
        return new File("/");
    }

    private File generateAcquisitionApplication(ApplicationDTO application) {
        return new File("/");
    }

    private File generateAddApplication(ApplicationDTO application) {
        return new File("/");
    }

    public File generateApplication(ApplicationDTO application) throws IOException {
        switch (application.getType()) {
            case ADD -> generateAddApplication(application);
            case WRITE_OFF -> generateWriteOffApplication(application);
            case ACQUISITION -> generateAcquisitionApplication(application);
            default -> throw new IllegalArgumentException("Unsupported type: " + application.getType());
        }
        return null;
    }
}
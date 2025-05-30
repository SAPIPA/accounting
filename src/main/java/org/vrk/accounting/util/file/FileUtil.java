package org.vrk.accounting.util.file;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.*;
import com.deepoove.poi.data.style.BorderStyle;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFTable.XWPFBorderType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.dto.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ru"));

    private File generateFIU10Act(ActDTO act) throws IOException {
        // 1) Собираем модель текстовых полей
        Map<String, Object> model = new HashMap<>();
        model.put("organization", act.getBody().get("organization"));
        LocalDate date = LocalDate.parse(act.getBody().get("date").toString());
        model.put("date", date.format(RUSSIAN_FORMATTER));
        model.put("mainEngineer", act.getBody().get("mainEngineer"));
        model.put("pernr", act.getBody().get("pernr"));
        model.put("snils", act.getBody().get("snils"));

        // 2) Достаём списки для таблицы и комиссии
        model.put("items", act.getBody().get("items"));
        model.put("commissionMembers", act.getBody().get("commissionMembers"));

        // 3) Настраиваем политику разворачивания циклов по тегам {{#items}} и {{#commissionMembers}}
        LoopRowTableRenderPolicy loopPolicy = new LoopRowTableRenderPolicy();
        Configure config = Configure.builder()
                .bind("items", loopPolicy)
                .bind("commissionMembers", loopPolicy)
                .build();

        // 4) (Опционально) Стиль границ таблиц
        BorderStyle borderStyle = new BorderStyle();
        borderStyle.setColor("A6A6A6");
        borderStyle.setSize(4);
        borderStyle.setType(XWPFBorderType.SINGLE);

        // 5) Подготовка выходного файла
        String filename = String.format("FIU10_act_%d.docx", act.getId());
        Path outputDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(outputDir);
        File outFile = outputDir.resolve(filename).toFile();

        // 6) Рендеринг шаблона с конфигурацией
        try (InputStream is = FIU10Template.getInputStream();
             XWPFTemplate tpl = XWPFTemplate.compile(is, config).render(model);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            tpl.write(fos);
        }

        return outFile;
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
        // 5) Путь вывода
        String filename = String.format("FMU73_act_%d.docx", act.getId());
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        File outFile = uploadPath.resolve(filename).toFile();
        // 6) Рендерим
        try (InputStream is = FMU73Template.getInputStream();
             XWPFTemplate tpl = XWPFTemplate.compile(is, config).render(model);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            tpl.write(fos);
        }
        return outFile;
    }

    private File generateServiceAct(ActDTO act) throws IOException {
        // 1) Собираем модель текстовых полей
        Map<String, Object> model = new HashMap<>();
        model.put("organization", act.getBody().get("organization"));

        LocalDate date = LocalDate.parse(act.getBody().get("date").toString());
        model.put("date", date.format(RUSSIAN_FORMATTER));

        model.put("itemName", act.getBody().get("itemName"));
        model.put("model", act.getBody().get("model"));
        model.put("serialNumber", act.getBody().get("serialNumber"));
        model.put("serviceNumber", act.getBody().get("serviceNumber"));

        model.put("installationText", act.getBody().get("installationText"));
        model.put("conclusionText", act.getBody().get("conclusionText"));

        // 2) Список членов комиссии для блока {{#commissionMembers}}
        model.put("commissionMembers", act.getBody().get("commissionMembers"));

        // 3) Настраиваем политику для развёртки списка commissionMembers
        LoopRowTableRenderPolicy loopPolicy = new LoopRowTableRenderPolicy();
        Configure config = Configure.builder()
                .bind("commissionMembers", loopPolicy)
                .build();

        // 4) Подготовка выходного файла
        String filename = String.format("SERVICE_act_%d.docx", act.getId());
        Path outputDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(outputDir);
        File outFile = outputDir.resolve(filename).toFile();

        // 5) Рендеринг шаблона
        try (InputStream is = ServiceTemplate.getInputStream();
             XWPFTemplate tpl = XWPFTemplate.compile(is, config).render(model);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            tpl.write(fos);
        }

        return outFile;
    }

    public File generateAct(ActDTO act) throws IOException {
        switch (act.getType()) {
            case FIU10 -> generateFIU10Act(act);
            case FMU73 -> generateFMU73Act(act);
            case SERVICE -> generateServiceAct(act);
            default -> throw new IllegalArgumentException("Unsupported type: " + act.getType());
        }
        return null;
    }


    public File generateWriteOffApplication(ApplicationDTO dto) throws IOException {
        // 1) Получаем список списываемых предметов из body
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items =
                (List<Map<String, Object>>) dto.getBody().get("items");

        // 2) Формируем заголовок таблицы: 4 колонки, высота 2.5 см (в пунктах ~180)
        RowRenderData header = Rows.of("Наименование", "Инв. номер", "Ед. изм.", "Кол-во")
                .textColor("FFFFFF")
                .bgColor("C00000")
                .center()
                .rowHeight(2.5f)
                .create();

        // 3) Строим строки данных
        List<RowRenderData> allRows = new ArrayList<>();
        allRows.add(header);
        for (Map<String, Object> item : items) {
            RowRenderData row = Rows.create(
                    Objects.toString(item.get("name"), ""),
                    Objects.toString(item.get("inventoryNumber"), ""),
                    Objects.toString(item.get("measuringUnit"), ""),
                    Objects.toString(item.get("count"), "")
            );
            allRows.add(row);
        }

        // 4) Собираем TableRenderData через Tables.create(...)
        TableRenderData table = Tables.create(allRows.toArray(new RowRenderData[0]));

        // 5) Модель для POI-TL
        Map<String, Object> model = new HashMap<>();
        model.put("itemTable", table);

        // 6) Подготовка выходного файла
        String filename = String.format("WRITE_OFF_application_%d.docx", dto.getId());
        Path outputDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(outputDir);
        File outFile = outputDir.resolve(filename).toFile();

        // 7) Рендеринг шаблона
        try (InputStream is = WriteOffTemplate.getInputStream();
             XWPFTemplate tpl = XWPFTemplate.compile(is).render(model);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            tpl.write(fos);
        }

        return outFile;
    }

    private File generateAcquisitionApplication(ApplicationDTO dto) throws IOException {
        // 1) Достаём поля из body
        String organization = (String) dto.getBody().get("organization");
        String mainEngineer = (String) dto.getBody().get("mainEngineer");
        String pernr = (String) dto.getBody().get("pernr");
        String snils = (String) dto.getBody().get("snils");
        String itemName = (String) dto.getBody().get("itemName");
        String reason = (String) dto.getBody().get("reason");

        // 2) Собираем модель для шаблона
        Map<String, Object> model = new HashMap<>();
        model.put("organization", organization);
        model.put("mainEngineer", mainEngineer);
        model.put("pernr", pernr);
        model.put("snils", snils);
        model.put("itemName", itemName);
        model.put("reason", reason);

        // 3) Подготовка выходного файла
        String filename = String.format("ACQUISITION_application_%d.docx", dto.getId());
        Path outputDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(outputDir);
        File outFile = outputDir.resolve(filename).toFile();

        // 4) Компиляция и рендеринг шаблона
        try (InputStream is = AcquisitionTemplate.getInputStream();
             XWPFTemplate tpl = XWPFTemplate.compile(is).render(model);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            tpl.write(fos);
        }

        return outFile;
    }

    private File generateAddApplication(ApplicationDTO dto) throws IOException {
        // 1) Забираем данные из dto.body
        String itemName = (String) dto.getBody().get("itemName");
        String reason = (String) dto.getBody().get("reason");
        String photoFilename = (String) dto.getBody().get("photoFilename");

        // 2) Полный путь до файла
        Path imgPath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(photoFilename);
        // проверяем что файл существует
        if (!Files.exists(imgPath)) {
            throw new FileNotFoundException("Photo not found: " + imgPath);
        }

        // 3) Собираем модель для шаблона
        Map<String, Object> model = new HashMap<>();
        model.put("item",   itemName);
        model.put("reason", reason);

        // Согласно POI-TL 3.2, для вставки изображения в шаблоне через {{@photo}}
        model.put("@photo",
                Pictures
                        .ofLocal(imgPath.toString())            // путь к локальному файлу
                        .size(200, 200)           // размер в пунктах (по умолчанию dpi=96)
                        .create()
        );

        // 4) Подготовка выходного файла
        String filename = String.format("ADD_application_%d.docx", dto.getId());
        Path outputDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(outputDir);
        File outFile = outputDir.resolve(filename).toFile();

        // 5) Компиляция и рендеринг шаблона
        try (InputStream is = AddTemplate.getInputStream();
             XWPFTemplate tpl = XWPFTemplate.compile(is).render(model);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            tpl.write(fos);
        }

        return outFile;
    }

    private File generateTransferApplication(ApplicationDTO dto) throws IOException {
        // 1) Извлекаем пользователя и список элементов из тела DTO
        String user = (String) dto.getBody().get("user");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items =
                (List<Map<String, Object>>) dto.getBody().get("items");

        // 2) Формируем заголовок таблицы: 4 колонки, цвет фона и текста, выравнивание и высота
        RowRenderData header = Rows.of("Наименование", "Инв. номер", "Ед. изм.", "Кол-во")
                .textColor("FFFFFF")
                .bgColor("4472C4")
                .center()
                .rowHeight(2.5f)
                .create();

        // 3) Строим массив строк: в первой ячейке — заголовок, далее — данные
        RowRenderData[] rows = new RowRenderData[items.size() + 1];
        rows[0] = header;
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            rows[i + 1] = Rows.create(
                    Objects.toString(item.get("name"), ""),
                    Objects.toString(item.get("inventoryNumber"), ""),
                    Objects.toString(item.get("measuringUnit"), ""),
                    Objects.toString(item.get("count"), "")
            );
        }

        // 4) Создаём TableRenderData через фабрику Tables.create(...)
        TableRenderData table = Tables.create(rows);

        // 5) Подготавливаем модель для рендеринга
        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        model.put("itemTable", table);

        // 6) Генерируем выходной файл
        Path outputDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(outputDir);
        File outFile = outputDir.resolve(
                String.format("TRANSFER_application_%d.docx", dto.getId())
        ).toFile();

        // 7) Компиляция и рендеринг шаблона
        try (InputStream is = TransferTemplate.getInputStream();
             XWPFTemplate tpl = XWPFTemplate.compile(is).render(model);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            tpl.write(fos);
        }

        return outFile;
    }

    public File generateApplication(ApplicationDTO application) throws IOException {
        switch (application.getType()) {
            case ADD -> generateAddApplication(application);
            case WRITE_OFF -> generateWriteOffApplication(application);
            case ACQUISITION -> generateAcquisitionApplication(application);
            case TRANSFER -> generateTransferApplication(application);
            default -> throw new IllegalArgumentException("Unsupported type: " + application.getType());
        }
        return null;
    }

    public File generateInventoryList(InventoryDTO inventory,
                                      Map<UUID, String> commissionNames,
                                      Map<Long, ItemDTO> itemIndex) throws IOException {
        // 1) Заполняем простые поля
        Map<String, Object> model = new HashMap<>();
        LocalDateTime start = inventory.getStartDate();
        LocalDateTime end   = inventory.getEndDate();
        model.put("startDate", start.format(DATE_FORMATTER));
        model.put("endDate",   end  .format(DATE_FORMATTER));

        // 2) Разворачиваем список ФИО членов комиссии
        //    Предполагаем, что commissionNames содержит маппинг UUID → ФИО
        List<String> commissionMembers = inventory.getCommissionMemberIds().stream()
                .map(commissionNames::get)
                .collect(Collectors.toList());
        model.put("commissionMembers", commissionMembers);

        // 3) Собираем список позиций описи для таблицы
        //    Каждая InventoryListDTO дополняется детальной инфой из itemIndex
        List<Map<String, Object>> rows = new ArrayList<>();
        for (InventoryListDTO entry : inventory.getInventoryLists()) {
            ItemDTO item = itemIndex.get(entry.getItemId());
            Map<String, Object> row = new HashMap<>();
            row.put("name",            item.getName());
            row.put("inventoryNumber", item.getInventoryNumber());
            row.put("isPresent",       entry.isPresent() ? "Да" : "Нет");
            row.put("note",            entry.getNote() == null ? "" : entry.getNote());
            rows.add(row);
        }
        model.put("items", rows);

        // 4) Привязываем политику развёртывания циклов по тегам {{#items}} и {{#commissionMembers}}
        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
        Configure config = Configure.builder()
                .bind("items", policy)
                .bind("commissionMembers", policy)
                .build();

        // 5) Подготавливаем выходной файл
        String filename = String.format("InventoryList_%d.docx", inventory.getId());
        Path outputDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(outputDir);
        File outFile = outputDir.resolve(filename).toFile();

        // 6) Рендерим шаблон
        try (InputStream is = InventoryListTemplate.getInputStream();
             XWPFTemplate tpl = XWPFTemplate.compile(is, config).render(model);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            tpl.write(fos);
        }

        return outFile;
    }

}
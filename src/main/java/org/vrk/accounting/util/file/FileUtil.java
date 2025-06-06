package org.vrk.accounting.util.file;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.Pictures;
import com.deepoove.poi.data.style.BorderStyle;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFTable.XWPFBorderType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Item;
import org.vrk.accounting.domain.dto.*;
import org.vrk.accounting.repository.ItemEmployeeRepository;
import org.vrk.accounting.repository.ItemRepository;
import org.vrk.accounting.service.EmployeeService;
import org.vrk.accounting.service.ItemService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileUtil {

    private final EmployeeService employeeService;
    private final ItemEmployeeRepository itemEmployeeRepository;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

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

    private File generateServiceAct(ActDTO dto) throws IOException {
        String itemName = (String) dto.getBody().get("itemName");
        String inventoryNumber = (String) dto.getBody().get("inventoryNumber");
        String serviceNumber = (String) dto.getBody().get("serviceNumber");

        Map<String, Object> model = new HashMap<>();
        model.put("itemName", itemName);
        model.put("inventoryNumber", inventoryNumber);
        model.put("serviceNumber", serviceNumber);

        String filename = String.format("SERVICE_act_%d.docx", dto.getId());
        Path outputDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(outputDir);
        File outFile = outputDir.resolve(filename).toFile();

        try (InputStream is = ServiceTemplate.getInputStream();
             XWPFTemplate tpl = XWPFTemplate.compile(is).render(model);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            tpl.write(fos);
        }

        return outFile;
    }

    public File generateAct(ActDTO act) throws IOException {
        return switch (act.getType()) {
            case FIU10 -> generateFIU10Act(act);
            case FMU73 -> generateFMU73Act(act);
            case SERVICE -> generateServiceAct(act);
            default -> throw new IllegalArgumentException("Unsupported type: " + act.getType());
        };
    }


    public File generateWriteOffApplication(ApplicationDTO dto) throws IOException {
        String itemName = (String) dto.getBody().get("itemName");
        String reason = (String) dto.getBody().get("reason");

        // 3) Собираем модель для шаблона
        Map<String, Object> model = new HashMap<>();
        model.put("item",   itemName);
        model.put("reason", reason);

        String filename = String.format("WRITE_OFF_application_%d.docx", dto.getId());
        Path outputDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(outputDir);
        File outFile = outputDir.resolve(filename).toFile();

        try (InputStream is = WriteOffTemplate.getInputStream();
             XWPFTemplate tpl = XWPFTemplate.compile(is).render(model);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            tpl.write(fos);
        }

        return outFile;
    }

    private File generateAcquisitionApplication(ApplicationDTO dto) throws IOException {
        String itemName = (String) dto.getBody().get("requestedItem");
        String reason = (String) dto.getBody().get("reason");

        Map<String, Object> model = new HashMap<>();
        model.put("item", itemName);
        model.put("reason", reason);

        String filename = String.format("ACQUISITION_application_%d.docx", dto.getId());
        Path outputDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(outputDir);
        File outFile = outputDir.resolve(filename).toFile();

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
        String photoFilename = (String) dto.getBody().get("file");

        // 2) Полный путь до файла
        Path imgPath = Paths.get(uploadDir)                      // корневая директория, например "/uploads"
                .toAbsolutePath()
                .normalize()
                .resolve("items")                     // добавляем папку "items"
                .resolve("temp")                       // добавляем вложенную папку "tmp"
                .resolve(photoFilename);              // затем имя файла        // проверяем что файл существует
        if (!Files.exists(imgPath)) {
            throw new FileNotFoundException("Photo not found: " + imgPath);
        }

        // 3) Собираем модель для шаблона
        Map<String, Object> model = new HashMap<>();
        model.put("item",   itemName);
        model.put("reason", reason);

        // Согласно POI-TL 3.2, для вставки изображения в шаблоне через {{@photo}}
        model.put("photo",
                Pictures
                        .ofLocal(imgPath.toString())
                        .size(200, 200)
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

        String itemName = (String) dto.getBody().get("itemName");
        String inventoryNumber = (String) dto.getBody().get("inventoryNumber");
        String toEmployeeName = (String) dto.getBody().get("toEmployeeName");
        String reason = (String) dto.getBody().get("reason");


        Map<String, Object> model = new HashMap<>();
        model.put("itemName", itemName);
        model.put("inventoryNumber", inventoryNumber);
        model.put("toEmployeeName", toEmployeeName);
        model.put("reason", reason);

        String filename = String.format("TRANSFER_application_%d.docx", dto.getId());
        Path outputDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(outputDir);
        File outFile = outputDir.resolve(filename).toFile();

        try (InputStream is = TransferTemplate.getInputStream();
             XWPFTemplate tpl = XWPFTemplate.compile(is).render(model);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            tpl.write(fos);
        }

        return outFile;
    }

    public File generateApplication(ApplicationDTO application) throws IOException {
        return switch (application.getType()) {
            case ADD         -> generateAddApplication(application);
            case WRITE_OFF   -> generateWriteOffApplication(application);
            case ACQUISITION -> generateAcquisitionApplication(application);
            case TRANSFER    -> generateTransferApplication(application);
            default -> throw new IllegalArgumentException("Unsupported type: " + application.getType());
        };
    }

    public File generateInventoryList(InventoryDTO dto) throws IOException {
        // 1) Сразу собираем «простые» поля
        LocalDate startDate = LocalDate.from(dto.getStartDate());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formatted = startDate.format(formatter);
        ItemEmployeeDTO responsibleEmployee = employeeService.toDto(
                itemEmployeeRepository.findById(dto.getResponsibleEmployeeId()).orElseThrow());
        String responsibleEmployeeName = responsibleEmployee.getLastName()
                + " " + responsibleEmployee.getFirstName()
                + " " + responsibleEmployee.getLastName();
        String responsibleEmployeePosition = responsibleEmployee.getPlans();

        ItemEmployeeDTO commissionChairman = employeeService.toDto(
                itemEmployeeRepository.findById(dto.getCommissionChairman()).orElseThrow());
        String commissionChairmanName = commissionChairman.getLastName()
                + " " + commissionChairman.getFirstName()
                + " " + commissionChairman.getLastName();
        String commissionChairmanPosition = commissionChairman.getPlans();

        // 2) Формируем список items: List<Map<String, Object>>
        List<InventoryListDTO> inventoryLists = dto.getInventoryLists();
        List<Map<String, Object>> itemsData = new ArrayList<>();

        if (inventoryLists != null && !inventoryLists.isEmpty()) {
            int counter = 1;
            for (InventoryListDTO il : inventoryLists) {
                // Получаем информацию об объекте (ItemDTO), чтобы взять его название
                ItemDTO itemDto = itemService.toDto(
                        itemRepository.findById(il.getItemId()).orElseThrow());
                String presentText = il.isPresent() ? "Да" : "Нет";

                Map<String, Object> row = new HashMap<>();
                row.put("index", counter++);
                row.put("name", itemDto.getName());
                row.put("serviceNumber", itemDto.getInventoryNumber());
                row.put("cost", itemDto.getCost());
                row.put("present", presentText);
                row.put("note", il.getNote().isEmpty() ? "-" : il.getNote());
                itemsData.add(row);
            }
        }

        // 3) Собираем модель и привязываем политику LoopRowTableRenderPolicy
        Map<String, Object> model = new HashMap<>();
        model.put("startDate", formatted);
        model.put("responsibleEmployeeName", responsibleEmployeeName);
        model.put("responsibleEmployeePosition", responsibleEmployeePosition);
        model.put("commissionChairmanName", commissionChairmanName);
        model.put("commissionChairmanPosition", commissionChairmanPosition);

        // Кладём уже готовый список в модель под ключом "items"
        model.put("items", itemsData);

        // Создаём политику, чтобы POI-TL «завернул» вторую строку таблицы в цикл
        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
        Configure config = Configure.builder()
                .bind("items", policy)
                .build();

        // 4) Стиль границ (если в шаблоне нужны рамки; опционально — можно убрать)
        BorderStyle borderStyle = new BorderStyle();
        borderStyle.setColor("A6A6A6");
        borderStyle.setSize(4);
        borderStyle.setType(XWPFBorderType.SINGLE);
        // Важно: саму политику рамки можно связать в шаблоне прямо через тег,
        // если вам надо, чтобы каждая строка получала одинаковую рамку.
        // Например, в шаблоне вместо {{index}} вы можете писать {{@tableLoop:items,index}};
        // тогда рамки будут применяться автоматически.
        // Если же всё ещё хотите настраивать рамки через Java,
        // то придётся использовать более сложные TableRenderPolicy.
        // Но в простом случае LoopRowTableRenderPolicy умеет копировать формат ячеек «как есть» из шаблона.

        // 5) Путь и имя выходного файла
        String filename = String.format("InventoryList_%d.docx", dto.getId());
        Path outputDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(outputDir);
        File outFile = outputDir.resolve(filename).toFile();

        // 6) Компиляция шаблона с указанной конфигурацией и рендеринг
        try (InputStream is = InventoryListTemplate.getInputStream();
             XWPFTemplate tpl = XWPFTemplate.compile(is, config).render(model);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            tpl.write(fos);
        }

        return outFile;
    }

}
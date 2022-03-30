package nl.ulivery.api.filestorage;

import nl.ulivery.api.filestorage.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class FileController {
    private final StorageService storageService;

    @Value("${SECRET_KEY}")
    private String secretKey;

    @Autowired
    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listFiles(Model model) {
        model.addAttribute("files", storageService.loadAll().map(path -> MvcUriComponentsBuilder.fromMethodName(FileController.class, "serveFile",
                path.getFileName().toString()).build().toUriString()).collect(Collectors.toList()));
        return "uploadFiles";
    }

    @GetMapping("/files/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        Resource file = storageService.loadAsResource(fileName);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("key") String key, RedirectAttributes redirectAttributes) {
        if (!Objects.equals(key, secretKey)) {
            redirectAttributes.addFlashAttribute("message", "Failed to upload file. Invalid API key supplied.");
            redirectAttributes.addFlashAttribute("class", "alert-danger");
        } else {
            storageService.store(file);
            redirectAttributes.addFlashAttribute("message", "File uploaded succesfully!");
            redirectAttributes.addFlashAttribute("class", "alert-success");
        }
        return "redirect:/";
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<?> handleException(FileNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}

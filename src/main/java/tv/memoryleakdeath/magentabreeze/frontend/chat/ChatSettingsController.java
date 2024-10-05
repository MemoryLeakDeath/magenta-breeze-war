package tv.memoryleakdeath.magentabreeze.frontend.chat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.backend.dao.ChatSettingsDao;
import tv.memoryleakdeath.magentabreeze.backend.integration.youtube.chat.YoutubeChatMessageEvent;
import tv.memoryleakdeath.magentabreeze.common.pojo.ChatSettings;
import tv.memoryleakdeath.magentabreeze.common.pojo.ChatSettingsRow;
import tv.memoryleakdeath.magentabreeze.frontend.BaseFrontendController;

@Controller
@RequestMapping("/settings/chat")
public class ChatSettingsController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(ChatSettingsController.class);

    @Autowired
    private ChatSettingsDao settingsDao;

    @Autowired
    private ChatSettingsValidator<ChatSettingsModel> settingsValidator;

    @Autowired
    private ApplicationContext context;

    @GetMapping("/")
    public String view(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "text.chat.title");
        try {
            List<ChatSettingsRow> allSettings = settingsDao.getAllSettings();
            model.addAttribute("allSettings", allSettings);
        } catch (Exception e) {
            logger.error("Unable to fetch all settings!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "settings/chat/chat";
    }

    @GetMapping("/create")
    public String createNew(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "text.chat.new.title");
        if (!model.containsAttribute("chatSettingsModel")) {
            model.addAttribute("chatSettingsModel", new ChatSettingsModel());
        }
        return "settings/chat/chat-create";
    }

    @PostMapping("/savenew")
    public String saveNew(HttpServletRequest request, Model model,
            @ModelAttribute ChatSettingsModel settings,
            BindingResult bindingResult) {
        try {
            settingsValidator.validate(request, settings, bindingResult);
            if (bindingResult.hasErrors()) {
                logger.error("Unable to save new chat settings, validation failed!");
                addErrorMessage(request, "text.error.systemerror");
                return createNew(request, model);
            }
            boolean success = settingsDao.createSettings(buildSettingsObject(settings));
            if (!success) {
                logger.error("Unable to save new chat settings!");
                addErrorMessage(request, "text.error.systemerror");
            } else {
                addSuccessMessage(request, "text.chat.success.created");
            }
        } catch (Exception e) {
            logger.error("Unable to save new chat settings!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "redirect:/settings/chat/";
    }

    private ChatSettingsRow buildSettingsObject(ChatSettingsModel model) {
        ChatSettingsRow row = new ChatSettingsRow();
        row.setActive(true);
        ChatSettings settings = new ChatSettings();
        settings.setChatFont(model.getChatFont());
        settings.setChatTextColor(model.getChatTextColor());
        settings.setChatTextSize(model.getChatTextSize());
        settings.setForceSmallEmotes(model.isForceSmallEmotes());
        settings.setShowServiceIcon(model.isShowServiceIcon());
        row.setName(model.getName());
        row.setSettings(settings);
        return row;
    }

    private ChatSettingsRow buildSettingsObjectForUpdate(ChatSettingsModel model) {
        ChatSettingsRow row = buildSettingsObject(model);
        row.setId(model.getId());
        return row;
    }

    @PostMapping("/edit")
    public String edit(HttpServletRequest request, Model model, @RequestParam(name = "id", required = true) Long id) {
        setPageTitle(request, model, "text.chat.edit.title");
        try {
            if (!model.containsAttribute("chatSettingsModel")) {
                ChatSettingsRow settings = settingsDao.getSettingsById(id);
                model.addAttribute("chatSettingsModel", buildModelObject(settings));
            }
        } catch (Exception e) {
            addErrorMessage(request, "text.error.systemerror");
            logger.error("Unable to edit chat panel with id: " + id, e);
        }
        return "settings/chat/chat-edit";
    }

    private ChatSettingsModel buildModelObject(ChatSettingsRow row) {
        ChatSettingsModel model = new ChatSettingsModel();
        if (row.getSettings() != null) {
            model.setChatFont(row.getSettings().getChatFont());
            model.setChatTextColor(row.getSettings().getChatTextColor());
            model.setChatTextSize(row.getSettings().getChatTextSize());
            model.setForceSmallEmotes(row.getSettings().isForceSmallEmotes());
            model.setShowServiceIcon(row.getSettings().isShowServiceIcon());
        }
        model.setId(row.getId());
        model.setName(row.getName());
        return model;
    }

    @PostMapping("/update")
    public String update(HttpServletRequest request, Model model, @ModelAttribute ChatSettingsModel settings,
            BindingResult bindingResult) {
        try {
            settingsValidator.validate(request, settings, bindingResult);
            if (bindingResult.hasErrors()) {
                logger.error("Unable to edit chat settings, validation failed!");
                addErrorMessage(request, "text.error.systemerror");
                return edit(request, model, settings.getId());
            }
            boolean success = settingsDao.updateSettings(buildSettingsObjectForUpdate(settings));
            if (!success) {
                logger.error("Unable to edit chat settings!");
                addErrorMessage(request, "text.error.systemerror");
            } else {
                addSuccessMessage(request, "text.chat.success.edited");
            }
        } catch (Exception e) {
            logger.error("Unable to edit chat panel settings!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "redirect:/settings/chat/";
    }

    @PostMapping("/toggleactive")
    public ResponseEntity<Void> toggleAlertStatus(HttpServletRequest request, Model model,
            @RequestParam(name = "id", required = true) Long id,
            @RequestParam(name = "active", required = true) boolean activeFlag) {
        try {
            boolean success = settingsDao.updateActiveFlag(id, activeFlag);
            if (!success) {
                logger.error("Unable to toggle chat panel status for id: {}", id);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            logger.error("Unable to toggle chat panel status for id: " + id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/delete")
    public String delete(HttpServletRequest request, Model model, @RequestParam(name = "id", required = true) Long id) {
        try {
            boolean success = settingsDao.deleteSettings(id);
            if (success) {
                addSuccessMessage(request, "text.chat.success.delete");
            } else {
                logger.error("unable to delete chat panel with id: {}", id);
                addErrorMessage(request, "text.error.systemerror");
            }
        } catch (Exception e) {
            logger.error("unable to delete chat panel with id: " + id, e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "redirect:/settings/chat/";
    }

    @GetMapping("/test/{id}")
    public String testChat(HttpServletRequest request, Model model, @PathVariable(name = "id") Long id) {
        YoutubeChatMessageEvent testPayload = new YoutubeChatMessageEvent();
        testPayload.setEventId(UUID.randomUUID().toString());
        testPayload.setChatMessage("This is a youtube test chat message!");
        testPayload.setAuthorName("test user");
        testPayload.setTimestamp(Instant.now().getEpochSecond());
        try {
            context.publishEvent(testPayload);
            addSuccessMessage(request, "text.chat.success.test");
        } catch (Exception e) {
            logger.error("Unable to publish test chat event!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "redirect:/settings/chat/";
    }

}

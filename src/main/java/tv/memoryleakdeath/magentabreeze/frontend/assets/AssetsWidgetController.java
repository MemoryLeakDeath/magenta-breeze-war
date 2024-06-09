package tv.memoryleakdeath.magentabreeze.frontend.assets;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.backend.dao.AssetsDao;
import tv.memoryleakdeath.magentabreeze.common.ContentTypeConstants;
import tv.memoryleakdeath.magentabreeze.common.pojo.Asset;
import tv.memoryleakdeath.magentabreeze.frontend.BaseFrontendController;
import tv.memoryleakdeath.magentabreeze.util.UploadUtil;

@Controller
@RequestMapping("/assets")
public class AssetsWidgetController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(AssetsWidgetController.class);

    @Autowired
    private AssetsDao assetsDao;

    @Autowired
    @Qualifier("installBaseDir")
    private String installDirectory;

    @GetMapping("/{type}")
    public String view(HttpServletRequest request, Model model, @PathVariable("type") String type) {
        viewPage(request, model, type, 1);
        return "assets/assets-widget";
    }

    @GetMapping("/{type}/{page}")
    public String viewPage(HttpServletRequest request, Model model, @PathVariable("type") String type,
            @PathVariable("page") int page) {
        try {
            model.addAttribute("searchType", type);
            List<String> searchTypes = switch (type) {
                case "image" -> ContentTypeConstants.IMAGE_CONTENT_TYPES;
                case "audio" -> ContentTypeConstants.AUDIO_CONTENT_TYPES;
                default -> null;
            };
            model.addAttribute("assetList", assetsDao.getAssetsByContentType(searchTypes, page));
            int totalPages = assetsDao.getAssetPageCountByType(searchTypes);
            if (page < totalPages) {
                model.addAttribute("nextPage", page + 1);
            }
        } catch (Exception e) {
            addErrorMessage(request, "text.error.systemerror");
            logger.error("Failed to view assets of type: " + type + " on page: " + page, e);
        }
        return "assets/assets-page";
    }

    @PostMapping("/upload")
    public String upload(HttpServletRequest request, Model model, @ModelAttribute AssetModel assetModel) {
        try {
            Asset newAsset = createAsset(assetModel);
            long newAssetId = assetsDao.createAsset(newAsset);
            if (!UploadUtil.uploadToFileSystem(assetModel.getAsset(), installDirectory, newAssetId)) {
                logger.error("Failed to upload asset id {} to filesystem!", newAssetId);
                model.addAttribute("uploadError", true);
            }
            model.addAttribute("newAssetId", newAssetId);
        } catch (Exception e) {
            logger.error("Failed to upload asset!", e);
            model.addAttribute("uploadError", true);
        }
        return view(request, model, assetModel.getType());
    }

    private Asset createAsset(AssetModel assetModel) {
        Asset newAsset = new Asset();
        newAsset.setContentType(assetModel.getAsset().getContentType());
        newAsset.setOriginalFilename(assetModel.getAsset().getOriginalFilename());
        newAsset.setDescription(assetModel.getDescription());
        return newAsset;
    }
}

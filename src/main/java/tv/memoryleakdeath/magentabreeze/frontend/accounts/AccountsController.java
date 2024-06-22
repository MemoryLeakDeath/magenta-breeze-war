package tv.memoryleakdeath.magentabreeze.frontend.accounts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.magentabreeze.backend.dao.AccountsDao;
import tv.memoryleakdeath.magentabreeze.frontend.BaseFrontendController;

@Controller
@RequestMapping("/settings/accounts")
public class AccountsController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

    @Autowired
    private AccountsDao accountsDao;

    @GetMapping("/")
    public String view(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "nav.accounts");
        try {
            model.addAttribute("accounts", accountsDao.getAllAccounts());
        } catch (Exception e) {
            logger.error("Unable to retrieve accounts!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "settings/accounts/accounts";
    }
}

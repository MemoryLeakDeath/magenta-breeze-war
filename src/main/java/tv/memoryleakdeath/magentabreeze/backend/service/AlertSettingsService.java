package tv.memoryleakdeath.magentabreeze.backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tv.memoryleakdeath.magentabreeze.backend.dao.AccountsDao;
import tv.memoryleakdeath.magentabreeze.backend.dao.AlertSettingsDao;
import tv.memoryleakdeath.magentabreeze.common.pojo.AlertSettingsRow;
import tv.memoryleakdeath.magentabreeze.common.pojo.AlertSettingsWithAssets;

@Service
public class AlertSettingsService {
    private static final Logger logger = LoggerFactory.getLogger(AlertSettingsService.class);

    @Autowired
    private AlertSettingsDao alertSettingsDao;

    @Autowired
    private AccountsDao accountsDao;

    public List<AlertSettingsRow> getAllSettingsWithAccounts() {
        List<AlertSettingsRow> settings = alertSettingsDao.getAllSettings();
        settings.forEach(alertSettings -> {
            alertSettings.setAccounts(accountsDao.getAccountsForAlert(alertSettings.getId()));
        });
        return settings;
    }

    public AlertSettingsWithAssets getSettingsWithAssetsAndAccounts(Long id) {
        AlertSettingsWithAssets settings = alertSettingsDao.getSettingsWithAssets(id);
        settings.setAccounts(accountsDao.getAccountsForAlert(id));
        return settings;
    }

}

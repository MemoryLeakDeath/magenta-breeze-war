package tv.memoryleakdeath.magentabreeze.common.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageMessages implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errorMessages = new ArrayList<>();
    private List<String> infoMessages = new ArrayList<>();
    private List<String> warningMessages = new ArrayList<>();
    private List<String> successMessages = new ArrayList<>();

    public void addErrorMessage(String messageKey) {
        errorMessages.add(messageKey);
    }

    public void addInfoMessage(String messageKey) {
        infoMessages.add(messageKey);
    }

    public void addWarningMessage(String messageKey) {
        warningMessages.add(messageKey);
    }

    public void addSuccessMessage(String messageKey) {
        successMessages.add(messageKey);
    }

    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }

    public boolean hasInfos() {
        return !infoMessages.isEmpty();
    }

    public boolean hasWarnings() {
        return !warningMessages.isEmpty();
    }

    public boolean hasSuccesses() {
        return !successMessages.isEmpty();
    }

    public List<String> viewErrors() {
        List<String> errors = List.copyOf(errorMessages);
        errorMessages.clear();
        return errors;
    }

    public List<String> viewInfos() {
        List<String> infos = List.copyOf(infoMessages);
        infoMessages.clear();
        return infos;
    }

    public List<String> viewWarnings() {
        List<String> warnings = List.copyOf(warningMessages);
        warningMessages.clear();
        return warnings;
    }

    public List<String> viewSuccesses() {
        List<String> successes = List.copyOf(successMessages);
        successMessages.clear();
        return successes;
    }
}

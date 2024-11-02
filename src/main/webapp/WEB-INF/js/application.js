// configure htmx
htmx.config.includeIndicatorStyles = false;

// configure jquery-minicolors (color picker)
$.minicolors.defaults = $.extend($.minicolors.defaults, {
	theme: 'bootstrap'
});

// Javascript triggered error/warning/info/success messages
function displayMessage(statusMessage, type, closeMsg) {
	let alertClass = "";
	let alertIconClass = "";
	switch(type) {
		case "error":
			alertClass = "alert-danger";
			alertIconClass = "fa-solid fa-triangle-exclamation";
			break;
		case "warning":
			alertClass = "alert-warning";
			alertIconClass = "fa-solid fa-circle-exclamation";		
			break;
		case "info":
			alertClass = "alert-info";
			alertIconClass = "fa-solid fa-circle-info";		
			break;
		case "success":
			alertClass = "alert-success";
			alertIconClass = "fa-solid fa-circle-check";		
			break;
		default: break;
	}
	let message = `
       <div class="alert ${alertClass} alert-dismissable" role="alert">
          <span>
             <i class="${alertIconClass}"></i>&nbsp;${statusMessage}
          </span>
          <button type="button" class="btn-close float-end" data-bs-dismiss="alert" aria-label="${closeMsg}"></button>
       </div>
	`;
	$('#topMessagesBox').append(message);
}

function showErrorMessage(message, closeMsg) {
	displayMessage(message, "error", closeMsg);
}

function showWarningMessage(message, closeMsg) {
	displayMessage(message, "warning", closeMsg);
}

function showInfoMessage(message, closeMsg) {
	displayMessage(message, "info", closeMsg);
}

function showSuccessMessage(message, closeMsg) {
	displayMessage(message, "success", closeMsg);
}

function escapeRegExp(string) {
  return string.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"); // $& means the whole matched string
}

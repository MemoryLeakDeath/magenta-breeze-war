$('#alert-text-color').minicolors({
	change: textColorChanged
});

function textColorChanged(hex, opacity) {
	$('#alert-text-color').val(hex);
}

$('body').on('click', '#image-select-button', handleImageSelect);
$('body').on('click', '#audio-select-button', handleAudioSelect);


function handleImageSelect() {
	var url = $('#image-select-modal').data('url');
	htmx.ajax('GET', url, '#image-select-modal-body').then(() => {
		$('#image-select-modal').modal('show');		
	});
}

function handleAudioSelect() {
	var url = $('#audio-select-modal').data('url');
	htmx.ajax('GET', url, '#audio-select-modal-body').then(() => {
		$('#audio-select-modal').modal('show');
	});
}

function handleSaveImage() {
	var selectedImageId = $('#image-select-modal input[name="selectedAsset"]:checked').val();
	$('#alert-image-id').val(selectedImageId);
	$('#image-select-modal').modal('hide');
}

function handleSaveAudio() {
	var selectedAudioId = $('#audio-select-modal input[name="selectedAsset"]:checked').val();
	$('#alert-audio-id').val(selectedAudioId);
	$('#audio-select-modal').modal('hide');
}
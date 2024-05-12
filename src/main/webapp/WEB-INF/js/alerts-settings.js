$('#alert-text-color').minicolors({
	change: textColorChanged
});

function textColorChanged(hex, opacity) {
	$('#alert-text-color').val(hex);
}

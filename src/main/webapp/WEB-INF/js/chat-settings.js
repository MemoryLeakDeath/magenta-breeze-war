$('#chat-text-color').minicolors({
	change: textColorChanged
});

function textColorChanged(hex, opacity) {
	$('#chat-text-color').val(hex);
}

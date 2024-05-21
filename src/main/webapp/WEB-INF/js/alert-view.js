
let alertAnimations = {};
$(document).ready(function() {
	let eventUrl = $('body').data('url');
	const eventSource = new EventSource(eventUrl);
	
	
	eventSource.addEventListener("trigger-alert", (event) => {
		let alertId = event.data;
		playAlert(alertId);
	});	
});

function playAlert(id) {
	const targetDiv = document.getElementById(`alert-${id}`);
	targetDiv
}

function startAnimation(targetDiv, visibleTime) {
	targetDiv.animate([{opacity: 0}, {opacity: 1}], {duration: 2000, fill: 'forwards'})
	         .onfinish = stayVisibleAnimationStep.apply(this, [targetDiv, parseInt(visibleTime)]); 
}

function stayVisibleAnimationStep(targetDiv, visibleTime) {
	targetDiv.animate([{opacity: 1}, {opacity: 1}], {duration: visibleTime, fill: 'forwards'})
	         .onfinish = fadeOutAnimationStep.apply(this, [targetDiv]);
}

function fadeOutAnimationStep(targetDiv) {
	targetDiv.animate([{opacity: 1}, {opacity: 0}], {duration: 2000, fill: 'forwards'});
}

function endAnimation(targetDiv) {
	targetDiv.style.display = 'none';
}
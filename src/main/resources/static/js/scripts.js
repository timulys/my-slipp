$(".answer-write input[type=submit]").click(addAnswer);

function addAnswer(e) {
	e.preventDefault();

	var queryString = $(".answer-write").serialize();
	console.log(queryString);

	var url = $(".answer-write").attr("action");

	$.ajax({
		type : "post",
		url : url,
		data : queryString,
		dataType : "json",
		error : onError,
		success : onSuccess
	});
}

function onError() {

}

function onSuccess(data, status) {
	console.log(data);
	var answerTemplate = $("#answerTemplate").html();
	var template = answerTemplate.format(data.writer.userId, data.formattedCreateDateTime, 
			data.contents, data.question.id, data.id);
	$(".qna-comment-slipp-articles").prepend(template);

	$(".answer-write textarea").val("");
}

$(document).on('click', '.link-delete-article', deleteAnswer);﻿

function deleteAnswer(e) {
	e.preventDefault();
	
	var deleteBtn = $(this);
	var url = deleteBtn.attr("href");
	
	$.ajax({
		type : "DELETE",
		url : url,
		dataType : "json",
		error: function (xhr, status) {
			console.log("error");
		},
		success : function (data, status) {
			if (data.valid) {
				deleteBtn.closest("article").remove();
			} else {
				alert(data.errorMessage);
			}
		}
	});
}

String.prototype.format = function() {
	var args = arguments;
	return this.replace(/{(\d+)}/g, function(match, number) {
		return typeof args[number] != 'undefined' ? args[number] : match;
	});
};
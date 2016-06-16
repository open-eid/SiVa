hljs.initHighlightingOnLoad();
Dropzone.autoDiscover = false;

Dropzone.options.sivaDropzone = {
    maxFilesize: 11
};

var sivaDropone = new Dropzone('#siva-dropzone');
sivaDropone.on('complete', function () {
    sivaDropone.removeAllFiles();
});

sivaDropone.on('sending', function () {
    $('#result-area, #validation-summery').addClass("hide");
});

sivaDropone.on('success', function (file, response) {
    $('#result-area').removeClass('hide');

    $('#validation-report').text(response.validationResult);
    $('#validation-report').each(function (i, block) {
        hljs.highlightBlock(block);
    });

    if (response.filename !== '') {
        $('#validation-summery').removeClass('hide');
        $('#document-name').text(response.filename);
        $('#overall-validation-result')
            .removeClass('invalid')
            .removeClass('valid')
            .addClass(response.overAllValidationResult.toLowerCase())
            .text(response.overAllValidationResult);
    }
});
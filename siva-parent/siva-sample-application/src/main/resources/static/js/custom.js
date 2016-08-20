(function ($) {
    hljs.initHighlightingOnLoad();
    Dropzone.autoDiscover = false;

    Dropzone.options.sivaDropzone = {
        maxFiles: 1,
        maxFilesize: 11,
        dictDefaultMessage: 'Drop files here or click to browse for upload file'
    };

    var sivaDropzone = new Dropzone('#siva-dropzone');
    sivaDropzone.on('complete', function () {
        sivaDropzone.removeAllFiles();
    });

    sivaDropzone.on('sending', function () {
        $('#result-area, #validation-summery').addClass("hide");
    });

    sivaDropzone.on('success', function (file, response) {
        $('#result-area').removeClass('hide');

        $('#validation-report').text(response.jsonValidationResult);
        $('#validation-report').each(function (i, block) {
            hljs.highlightBlock(block);
        });

        $('#soap-validation-report').text(response.soapValidationResult);
        $('#soap-validation-report').each(function (i, block) {
            hljs.highlightBlock(block);
        });

        console.log(response);
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
})(jQuery);

(function ($) {
    hljs.initHighlightingOnLoad();
    Dropzone.autoDiscover = false;

    Dropzone.options.sivaDropzone = {
        maxFiles: 1,
        maxFilesize: 11,
        previewTemplate : '<progress class="progress progress-info progress-striped" id="file-progress" value="0" max="100"></progress>',
        dictDefaultMessage: 'Drop files here or click to browse for upload file'
    };

    var sivaDropzone = new Dropzone('#siva-dropzone');
    sivaDropzone.on('complete', function () {
        sivaDropzone.removeAllFiles();
    });

    sivaDropzone.on('sending', function () {
        $('#result-area, #validation-summery').addClass("hide");
    });

    sivaDropzone.on('uploadprogress', function (file, progress) {
        for (var i = 0; i <= 100; i++) {
            $('#file-progress').attr('value', i);
        }
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

    if (Cookies.get('notification') === undefined) {
        $('#notification').show();
    }

    $('#notification').find('button.close').click(function(e) {
        e.preventDefault();
        Cookies.set('notification', 'closed', {path: '/'});
    });
})(jQuery);

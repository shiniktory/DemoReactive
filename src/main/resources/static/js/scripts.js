function publishNewPost(newsObject) {
    var newPostHtml = '<div class="card container-fluid w-75">' +
        '<h5 class="card-title mt-2" style="font-size: 26px;">' + newsObject.type + '</h5>' +
        '<img class="card-img-top " src="' + newsObject.artist.imagePath + '" alt="Artist image">' +
        '<div class="card-body w-100">' +
        '    <span class="card-text" style="font-size: 24px;">' + newsObject.text + '</span>' +
        '  </div>' +
        '  <ul class="list-group list-group-flush w-100">' +
        '    <li class="list-group-item w-100" style="font-size: 26px;"><h3>Artist: </h3>' + newsObject.artist.artistName + '</li>' +
        '    <li class="list-group-item w-100 font-italic">Published: ' + new Date(newsObject.publishedTime).toLocaleString() + '</li>' +
        '  </ul>' +
        '</div><br>';

    $('#news_content_inner').prepend(newPostHtml);
}

function updateArtistTop() {
    $.get('/update_top', function (response) {
        $('#top_tbody').html($(response).html());
    });
}

function artistPreview() {
    var selectedArtistId = $('#artistSelect').val();

    if (selectedArtistId !== '') {
        $('#selected_artist').load('/find_artist/' + selectedArtistId + ' #selected_artist_inner');
    } else {
        $('#selected_artist_inner').hide();
    }
}

function postNews() {
    var posting = $.post('/add_news', $('#new_message_form').serializeArray());

    posting.done(function () {
        $('#message_input').val('');
        $('#selected_artist_inner').hide();
        $('#newsTypeSelect').val('');
        $('#artistSelect').val('');

    });

    posting.fail(function () {
        console.log('Cannot send text...');
    });

    return false;
}


var newsSource;


function startNewsStream(sourceUrl) {

    newsSource = new EventSource(sourceUrl);

    newsSource.onmessage = function (event) {
        var newsObject = JSON.parse(event.data);

        console.log(newsObject);

        publishNewPost(newsObject);

        updateArtistTop();
    };

    newsSource.onerror = function (e) {
        console.log(e.message);
        stopNewsStream();
    };
}

function stopNewsStream() {
    newsSource.close();
}

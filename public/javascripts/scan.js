$(document).ready(function(){

    var keywords = new Set();
    var slider = document.getElementById('sentimentSlider');
    var tweetsTable = $('#tweetsTable').DataTable({
        columnDefs: [
            { targets: [2, 3], visible: false},
            { "width": "80%", "targets": 0 },
            { "width": "20%", "targets": 1 },
        ]
    });
    // Custom function to filter table based on sentiment value
    // https://datatables.net/examples/plug-ins/range_filtering.html
    $.fn.dataTable.ext.search.push(
        function( settings, data, dataIndex ) {
            var max = parseInt( slider.noUiSlider.get(), 10 );
            var sentiment = parseFloat( data[2] ) || 0; // use data for the sentiment column

            if ( sentiment <= max ) return true
            return false;
        }
    );

    function filterColumn ( col, value ) {
        tweetsTable.column( col ).search(
            value,
            true, // Use regex
            true, // Use smart search
        ).draw();
    }

    $(function() {
      // KEYWORDS

      $("#add-keyword-input").on({
        focusout : function() {
          var txt = this.value.replace(/[^a-z0-9\+\-\.\#]/ig,''); // allowed characters
          if(txt) {
            // Add keyword to list of keywords
            keywords.add(txt.toLowerCase())
            // display new keyword
            $("<span/>", {text:txt.toLowerCase(), appendTo:"#keyword-container", class:"dashfolio-keyword"});
            // Filter table using keyword
            filterColumn(0, Array.from(keywords).join("|"));
          }

          this.value = "";
        },
        keyup : function(ev) {
          // if: comma|enter (delimit more keyCodes with | pipe)
          if(/(188|13)/.test(ev.which)) $(this).focusout();
        }
      });

      $('.keyword-container').on('click', 'span', function() {
        console.log($(this).text())
        keywords.delete($(this).text());
        console.log(keywords);
        filterColumn(0, Array.from(keywords).join("|"));
        $(this).remove();
      });
    });

    $(function() {
        // SENTIMENT SLIDER

        var sentimentValueTags = {
            '0': 'Strong negative',
            '1': 'Negative',
            '2': 'Neutral',
            '3': 'Positive',
            '4': 'Strong positive'
        };
        noUiSlider.create(slider, {
            start: 2,
            step: 1,
            orientation: 'vertical',
            direction: 'rtl',
            tooltips: false,
            range: {
                'min': 0,
                'max': 4
            },
            pips: { // Show a scale with the slider
                mode: 'steps',
                stepped: true,
                density: 25,
                format: {
                    to: function(a) {
                        return sentimentValueTags[a];
                    }
                }
            }
       });
        slider.noUiSlider.on('update', function() {
            // Redraw table so the new value is taken into account
            tweetsTable.draw();
        });
    });

    $(function() {
        // TWEETS API CALL

        var urlParams = new URLSearchParams(window.location.search);
        var decoder = new TextDecoder();

        fetch("/api/v1/scan/" + urlParams.get('user'))
            .then( (response) => { console.log(response); return response.body.getReader(); })
            .then( reader => {
                function processChunk() {
                    reader.read().then( ({ done, value }) => {
                        if (done) return;

                        let tweets = JSON.parse(decoder.decode(value, {stream: true})).tweets;
                        for (let element of tweets) {
                            tweetsTable.row.add([
                                element.text,
                                new Date(element.created_at).toUTCString(),
                                element.sentiment,
                                element.cleaned_text
                            ]).draw( false );
                        }
                        return processChunk();
                    });
                }
                return processChunk();
            });
    });
});
var cheerio = require('cheerio');
var request = require('request');

var url = 'https://unipass.customs.go.kr:38010/ext/rest/cargCsclPrgsInfoQry/retrieveCargCsclPrgsInfo?crkyCn=h230p129d140a351h010u090b0&hblNo=6095003067487&blYy=2019';

var state = new Array(),
    location = new Array(),
    date = new Array();



request(url, function(error, response, html){
        var $ = cheerio.load(html);

            $("cargCsclPrgsInfoDtlQryVo").each(function(index) {
                let title = $(this).find('cargTrcnRelaBsopTpcd').text()
                state[index] = title
            })






        for (var i= 0; i < state.length ; i++) {
            console.log(state[i]);
        }

});


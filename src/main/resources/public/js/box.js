/**
 * Copyright 2019 ISTAT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence. You may
 * obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * Licence for the specific language governing permissions and limitations under
 * the Licence.
 *
 * @author Francesco Amato <framato @ istat.it>
 * @author Mauro Bruno <mbruno @ istat.it>
 * @author Paolo Francescangeli  <pafrance @ istat.it>
 * @author Renzo Iannacone <iannacone @ istat.it>
 * @author Stefano Macone <macone @ istat.it>
 * @version 1.0
 */
var smallWindow = 560;
var load = true;

$(document).ajaxError(function (e, request, errorThrown, exception) {
    if (request.status == "302") {
        window.location = request.getResponseHeader('location');
    }
});

function doToggling(w) {
    if (w <= smallWindow) {
        $("aside").addClass("toggle-off");
        $("section").addClass("toggle-off");
        $("footer").addClass("toggle-off");
    } else {
        $("aside").fadeIn();
        $("aside").removeClass("toggle-off");
        $("section").removeClass("toggle-off");
        $("footer").removeClass("toggle-off");
    }
}

function toggleMenu() {
    var w = 0;
    if (load) {
        w = $(window).width();
    } else if ($("aside").hasClass("toggle-off")) {
        w = smallWindow + 1;
    }
    doToggling(w);
    load = false;
}



$(window).resize(function () {
    doToggling($(window).width());
});

$(function () {
    toggleMenu();
    $('.towait').click(function () {
        $('#loading').modal('show');
    });

    setTimeout(function () {
        $("#messages-container").fadeOut();
    }, 4000);

    $('.modal').modal({show: false, backdrop: 'static', keyboard: false})

});

//Set menu active
function setMenuActive(id) {
    $("#" + id).addClass("active");
    $("#" + id + " a").addClass('active').parent().parent().addClass('in').parent();
}


function format(n) {
    return ("" + n).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function formatPercentage(a, b) {
    return Math.round(1000. * a / b) / 10 + "%";
}

//write REST response user
function writeMsgs(data, iddiv_msgs) {
    $.each(data, function (index, msg) {
        var classs = 'alert alert-info';
        if (msg.type == 'INFO')
            classs = 'alert alert-success';
        else if (msg.type == 'ERROR')
            classs = 'alert alert-danger';
        var div = $('<div class="' + classs + '"><strong>' + msg.type
                + '</strong>: ' + msg.text + ' </div>"');
        $("#" + iddiv_msgs).append(div);

    });
}

//write REST response user
function writeMsgsError(msg, iddiv_msgs) {
    var classs = 'alert alert-danger';
    var div = $('<div class="' + classs + '">' + msg + ' </div>"');
    $("#" + iddiv_msgs).append(div);

}

//function to render table
function renderTable(id, defBtns, defCols, arrLabelData) {
    $("#" + id).DataTable({
        bDestroy: true,
        dom: "<'row'<'col-sm-6'B><'col-sm-6'f>>" +
                "<'row'<'col-sm-12'tr>>" +
                "<'row'<'col-sm-5'i><'col-sm-7'p>>",
        autoWidth: false,
        responsive: true,
        ordering: false,
        pageLength: 20,
        bPaginate: false,
        buttons: defBtns,
        columns: defCols,
        data: arrLabelData
    });
//table.buttons().container().appendTo('#religionlist_wrapper .col-sm-6:eq(0)');
}

function callBackHide() {
    setTimeout(function () {
        $("#center").fadeOut();
    }, 1000);
}

function callBackShow() {
    setTimeout(function () {
        $("#center").fadeIn();
    }, 1000);
}
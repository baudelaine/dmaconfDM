
$(document)
.ready(function() {
  getDatas();
})
.ajaxStart(function(){
    $("div#divLoading").addClass('show');
})
.ajaxStop(function(){
    $("div#divLoading").removeClass('show');
});

function computeQuery(){
  var query = $("#query").val();
  var parms = {query: query};
  GetSQLQuery(JSON.stringify(parms));
}

function eraseQuery(){
  $("#query").val("");
}

function getDatas(){
  var parms = localStorage.getItem('SQLQuery');
  console.log(parms);
  GetSQLQuery(parms);
}

function GetSQLQuery(parms){
  if(parms != 'null' && parms != null){
    $.ajax({
      type: 'POST',
      url: "GetSQLQuery",
      dataType: 'json',
      data: parms,

      success: function(datas) {
        console.log(datas);
        if(datas.STATUS == "OK"){
          var cols = [];
          // cols.push({field: "PROJNO", title: "PROJNO"});
          $.each(datas.columns, function(i, column_name){
            var col = {};
            col.field = column_name;
            col.title = column_name;
            col.sortable = true;
            cols.push(col);
          })
          $('#table').bootstrapTable('destroy');
          buildTable($('#table'), cols, datas.result);
        }
        else{
          showalert("ERROR: " + datas.EXCEPTION + "<br>" + datas.MESSAGE + "<br>" + datas.TROUBLESHOOTING, "alert-danger", "bottom");
        }
        $("#query").text(datas.query);
        $("#schema").text(datas.schema);

      },
      error: function(data) {
        console.log(data);
      }
    });
  }
}

function buildTable($el, cols, datas) {

    $el.bootstrapTable({
        columns: cols,
        // url: "js/projectTable.json",
        data: datas,
        toolbar: $("#watchToolbar"),
        search: true,
        searchOnEnterKey: true,
        showRefresh: false,
        showColumns: true,
        showToggle: false,
        pagination: false,
        pageSize: 25,
        showPaginationSwitch: false,
        paginationVAlign: "both",
        detailView: false
    })

    var $tableHeaders = $el.find('thead > tr > th');
    console.log('$tableHeaders');
    console.log($tableHeaders.eq(3));
    var cols = $el.bootstrapTable('getOptions').columns[0];
    console.log(cols);
}

function showalert(message, alertType, area) {

    $('#alertmsg').remove();

    var timeout = 5000;

    if(area == undefined){
      area = "bottom";
    }
    if(alertType.match('warning')){
      area = "bottom";
      timeout = 10000;
    }
    if(alertType.match('danger')){
      area = "bottom";
      timeout = 30000;
    }

    var $newDiv;

    if(alertType.match('alert-success|alert-info')){
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          message
        )
       .addClass('alert ' + alertType + ' flyover flyover-' + area);
    }
    else{
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
          '<strong>' + message + '</strong>'
        )
       .addClass('alert ' + alertType + ' alert-dismissible flyover flyover-' + area);
    }

    $('#Alert').append($newDiv);

    if ( !$('#alertmsg').is( '.in' ) ) {
      $('#alertmsg').addClass('in');

      setTimeout(function() {
         $('#alertmsg').removeClass('in');
      }, timeout);
    }
}

var currentProject;
var queriesList = [];
var searchCols = [];
searchCols.push({field:"table_name", title: "Table<br>Name", sortable: true, searchable: false});
searchCols.push({field:"table_type", title: "Table<br>Type", sortable: true, searchable: false});
searchCols.push({field:"table_remarks", title: "Table<br>Label", sortable: true, searchable: false});
searchCols.push({field:"table_description", title: "Table<br>Description", sortable: true, searchable: false});

searchCols.push({field:"table_primaryKeyFieldsCount", title: 'FPK<br><i class="glyphicon glyphicon-question-sign"></i>', titleTooltip: "Number of fields within primary key", sortable: true, searchable: false});
searchCols.push({field:"table_importedKeysCount", title: 'IPK<br><i class="glyphicon glyphicon-question-sign"></i>', titleTooltip: "Number of primary keys imported", sortable: true, searchable: false});
searchCols.push({field:"table_importedKeysSeqCount", title: 'IPKS<br><i class="glyphicon glyphicon-question-sign"></i>', titleTooltip: "Number of sequence within primary keys imported", sortable: true, searchable: false});
searchCols.push({field:"table_exportedKeysCount", title: 'EFK<br><i class="glyphicon glyphicon-question-sign"></i>', titleTooltip: "Number of foreign keys exported", sortable: true, searchable: false});
searchCols.push({field:"table_exportedKeysSeqCount", title: 'EFKS<br><i class="glyphicon glyphicon-question-sign"></i>', titleTooltip: "Number of sequence within foreign keys exported", sortable: true, searchable: false});
searchCols.push({field:"table_indexesCount", title: 'Indexes<br><i class="glyphicon glyphicon-question-sign"></i>', titleTooltip: "Number of indexed fields", sortable: true, searchable: false});
searchCols.push({field:"table_recCount", title: 'Records<br>Count<br><i class="glyphicon glyphicon-question-sign"></i>', titleTooltip: "Number of records in table", sortable: true, searchable: false});
searchCols.push({field:"table_fieldCount", title: 'Fields<br>Count<br><i class="glyphicon glyphicon-question-sign"></i>', titleTooltip: "Number of fields in table", sortable: true, searchable: false});

searchCols.push({field:"column_name", title: "Column<br>Name", sortable: true, searchable: false});
searchCols.push({field:"column_type", title: "Column<br>Type", sortable: true, searchable: false});
searchCols.push({field:"column_remarks", title: "Column<br>Label", sortable: true, searchable: false});
searchCols.push({field:"column_description", title: "Column<br>Description", sortable: true, searchable: false});
searchCols.push({field:"column_isPrimaryKey", title: "Primary<br>Key", formatter: "isPrimaryKeyFormatter", sortable: true, searchable: false});
searchCols.push({field:"column_isIndexed", title: "Indexed", formatter: "isIndexedFormatter", sortable: true, searchable: false});
searchCols.push({field:"column_isNullable", title: "Nullable", sortable: true, searchable: false});

function isPrimaryKeyFormatter(value, row, index) {
  var icon = value == true ? 'glyphicon-star' : ''
  if(value == undefined){
      icon = '';
  }
  return [
    '<a href="javascript:void(0)">',
    '<i class="glyphicon ' + icon + '"></i> ',
    '</a>'
  ].join('');
}

function isIndexedFormatter(value, row, index) {
  var icon = value == true ? 'glyphicon-star-empty' : ''
  if(value == undefined){
      icon = '';
  }
  return [
    '<a href="javascript:void(0)">',
    '<i class="glyphicon ' + icon + '"></i> ',
    '</a>'
  ].join('');
}

$(document)
.ready(function() {
  GetCurrentProject();
  buildTable($('#searchTable'), searchCols);
  checkDBMD();
  ChooseTable($('#searchSelect'));
  ChooseColumn($('#searchColumnSelect'));
  // GetDBMD($('#searchTable'));
})
.on('hidden.bs.modal', '.modal', function () {
    $('.modal:visible').length && $(document.body).addClass('modal-open');
})
.ajaxStart(function(){
    $("div#divLoading").addClass('show');
		$("div#modDivLoading").addClass('show');

})
.ajaxStop(function(){
    $("div#divLoading").removeClass('show');
    $("div#modDivLoading").removeClass('show');
});

$('#dynamicModal').on('shown.bs.modal', function(){

    console.log("dynamicModal shown.bs.modal");
    $(this).find('.modal-header').find('.modal-title').empty();
    $(this).find('.modal-body').empty();
    $(this).find('.modal-footer').empty();
    var list = '<div class="container-fluid"><div class="row"><form role="form"><div class="form-group">';
    list += '<input id="searchinput" class="form-control" type="search" placeholder="Search..." /></div>';
    list += '<div id="searchlist" class="list-group">';

    var title = "<h4>Choose a table and display its content.</h4>"
    var tables = [];

    $.ajax({
      type: 'POST',
      url: "GetDBMDFromCache",
      dataType: 'json',
      async: true,
      success: function(data) {
        console.log(data);
        if(Object.keys(data).length > 0){
          tables = Object.values(data.DATAS);
          console.log(tables);
          $.each(tables, function(i, obj){
            list += '<a href="#" id="' + obj.table_name +'" class="list-group-item"><span>' + obj.table_name + ' - ' + obj.table_remarks + ' - ' + obj.table_stats + '</span></a>';
          });
          list += '</div></form><script>';
          list += '$("#searchlist").btsListFilter("#searchinput", {itemChild: "span", initial: false, casesensitive: false,});';
          list += '$(".list-group a").click(function(){console.log($(this).attr("id")); WatchContent("select * from " + $(this).attr("id"))})';
          list += '</script>';
          $('#dynamicModal').find('.modal-header').find('.modal-title').append(title);
          $('#dynamicModal').find('.modal-body').append(list);
        }
        else {
          $.ajax({
          type: 'POST',
          url: "GetTables",
          dataType: 'json',
          async: true,
            success: function(data){
              tables = data;
              console.log(tables);
              $.each(tables, function(i, obj){
                list += '<a href="#" id="' + obj.name +'" class="list-group-item"><span>' + obj.name + '</span></a>';
              });
              list += '</div></form><script>';
              list += '$("#searchlist").btsListFilter("#searchinput", {itemChild: "span", initial: false, casesensitive: false,});';
              list += '$(".list-group a").click(function(){console.log($(this).attr("id")); WatchContent("select * from " + $(this).attr("id"))})';
              list += '</script>';
              $('#dynamicModal').find('.modal-header').find('.modal-title').append(title);
              $('#dynamicModal').find('.modal-body').append(list);
            }
          });
      }
    }
    });

});

function GetCurrentProject(){
  $.ajax({
    type: 'POST',
    url: "GetCurrentProject",
    dataType: 'json',
    async: true,
    success: function(data) {
      currentProject = data.data;
    },
    error: function(data) {
      console.log(data);
    }
  });
}


function promptTeasing(){

  bootbox.confirm({
    title: "Feature is not available",
    message: "Some features are available only when project is connected to a database.<br>Ask for more informations ?",
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Ask',
          className: 'btn btn-primary'
      }
    },
    callback: function(result){
      if(result){
        console.log("ask was clicked...");
        window.open("http://data-accelerator.com/contact/");
      }
    }
  });
}

$("#watchContent").click(function(){

  if(currentProject){
    console.log(currentProject.resource.jndiName);
    if(currentProject.resource.jndiName == "XML"){
      promptTeasing();  
    }
  }

  else{


  var table_name = $('#searchSelect').find("option:selected").val();
  if(!table_name == ""){
  var query = "select * from " + table_name;
  console.log(query);
  var parms = {query: query};
  localStorage.setItem('SQLQuery', JSON.stringify(parms));
  window.open("watchContent.html");
  }
  else{
    showalert("No Table selected.", "Select a table first.", "alert-warning", "bottom");
    $("#tables").selectpicker("toggle");
  }
  }
})

$('#modQueriesList').on('hidden.bs.modal', function() {
});

$('#modQueriesList').on('shown.bs.modal', function() {
  $(this).find('.modal-body').empty();
  var list = '<div class="container-fluid"><div class="row"><form role="form"><div class="form-group">';
  list += '<input id="searchinput" class="form-control" type="search" placeholder="Search..." /></div>';
  list += '<div id="searchlist" class="list-group">';

  $.each(queriesList, function(index, object){
    list += '<a href="#" class="list-group-item" onClick="OpenQueries(' + object.id + '); return false;"><span>' + object.name + '</span></a>';
  });
  list += '</div></form><script>$("#searchlist").btsListFilter("#searchinput", {itemChild: "span", initial: false, casesensitive: false});</script>';
  $(this).find('.modal-body').append(list);
});



$('#queryModal').on('shown.bs.modal', function() {
  if($('#searchSelect').val().length == 0){
    $('#searchSelect').selectpicker('selectAll');
  }
});

$('#queryModal').on('hidden.bs.modal', function() {
    $('#searchSelect').selectpicker('deselectAll');
    $('#queryModal .collapse').each(function () {
        $(this).collapse('hide');
    });
});

// START

$("#addSqlLabel").click(function(){
  if(currentProject){
    console.log(currentProject.resource.jndiName);
    if(currentProject.resource.jndiName == "XML"){
      promptTeasing();  
    }
    else{
      $('#queryModal').modal('toggle');
    }
  }
})

$("#addCsvLabel").click(function(){
  $('#csvLabelModal').modal('toggle');
})

$("#addCsvRel").click(function(){
  $("#csvRelationModal").modal('toggle');
})

$("#addCsvRelExp").click(function(){
  $("#csvRelationExpModal").modal('toggle');
})

$('#csvRelationModal').on('hidden.bs.modal', function() {
  $('#csvRelationModal .collapse').each(function () {
      $(this).collapse('hide');
  });
})

$('#csvRelationModal').on('shown.bs.modal', function() {
  $.ajax({
 		type: 'POST',
 		url: "GetCSVFirstRecords",
 		dataType: 'json',

 		success: function(data) {
      console.log(data);

      var $table = $("#csvRelationTable");
      $table.find("tr:gt(0)").remove();
      if(data.DATAS){
        if(data.DATAS.relation){
          $.each(data.DATAS.relation, function(i, record){
            $table.append($('<tr>')
            .append($('<td>').append(record.FK_NAME))
            .append($('<td>').append(record.PK_NAME))
            .append($('<td>').append(record.FKTABLE_NAME))
            .append($('<td>').append(record.PKTABLE_NAME))
            .append($('<td>').append(record.KEY_SEQ))
            .append($('<td>').append(record.FKCOLUMN_NAME))
            .append($('<td>').append(record.PKCOLUMN_NAME))
            )
          })
          $table.append($('<tr>')
            .append($('<td>').append("..."))
            .append($('<td>').append("..."))
            .append($('<td>').append("..."))
            .append($('<td>').append("..."))
            .append($('<td>').append("..."))
            .append($('<td>').append("..."))
            .append($('<td>').append("..."))
          )
          $('#delCsvRelation').prop('disabled', false);
        }
        else{
          $table.append($('<tr>')
            .append($('<td style="text-align: center; vertical-align: middle;" colspan="7">').append("no record found"))
          )
          $('#delCsvRelation').prop('disabled', true);
        }
      }
 		},
 		error: function(data) {
      console.log(data);
 		}
 	});

})

$('#csvLabelModal').on('hidden.bs.modal', function() {
    $('#searchSelect').selectpicker('deselectAll');
    $('#csvLabelModal .collapse').each(function () {
        $(this).collapse('hide');
    });

});


$('#csvLabelModal').on('shown.bs.modal', function() {

  if($('#searchSelect').val().length == 0){
    $('#searchSelect').selectpicker('selectAll');
  }

  $.ajax({
 		type: 'POST',
 		url: "GetCSVFirstRecords",
 		dataType: 'json',

 		success: function(data) {
      console.log(data);

      var $table = $("#csvTableLabelTable");
      $table.find("tr:gt(0)").remove();
      if(data.DATAS.tableLabel){
        $.each(data.DATAS.tableLabel, function(i, record){
          $table.append($('<tr>')
            .append($('<td>').append(record.tableName))
            .append($('<td>').append(record.tableLabel))
          )
        })
        $table.append($('<tr>')
          .append($('<td>').append("..."))
          .append($('<td>').append("..."))
        )
        $('#delCsvTableLabel').prop('disabled', false);
      }
      else{
        $table.append($('<tr>')
          .append($('<td style="text-align: center; vertical-align: middle;" colspan="2">').append("no record found"))
        )
        $('#delCsvTableLabel').prop('disabled', true);
      }

      $table = $("#csvTableDescriptionTable");
      $table.find("tr:gt(0)").remove();
      if(data.DATAS.tableDescription){
        $.each(data.DATAS.tableDescription, function(i, record){
          $table.append($('<tr>')
            .append($('<td>').append(record.tableName))
            .append($('<td>').append(record.tableDescription))
          )
        })
        $table.append($('<tr>')
          .append($('<td>').append("..."))
          .append($('<td>').append("..."))
        )
        $('#delCsvTableDescription').prop('disabled', false);
      }
      else{
        $table.append($('<tr>')
          .append($('<td style="text-align: center; vertical-align: middle;" colspan="2">').append("no record found"))
        )
        $('#delCsvTableDescription').prop('disabled', true);
      }

      $table = $("#csvColumnLabelTable");
      $table.find("tr:gt(0)").remove();
      if(data.DATAS.columnLabel){
        $.each(data.DATAS.columnLabel, function(i, record){
          $table.append($('<tr>')
            .append($('<td>').append(record.tableName))
            .append($('<td>').append(record.columnName))
            .append($('<td>').append(record.columnLabel))
          )
        })
        $table.append($('<tr>')
          .append($('<td>').append("..."))
          .append($('<td>').append("..."))
          .append($('<td>').append("..."))
        )
        $('#delCsvColumnLabel').prop('disabled', false);
      }
      else{
        $table.append($('<tr>')
          .append($('<td style="text-align: center; vertical-align: middle;" colspan="3">').append("no record found"))
        )
        $('#delCsvColumnLabel').prop('disabled', true);
      }

      $table = $("#csvColumnDescriptionTable");
      $table.find("tr:gt(0)").remove();
      if(data.DATAS.columnDescription){
        $.each(data.DATAS.columnDescription, function(i, record){
          $table.append($('<tr>')
            .append($('<td>').append(record.tableName))
            .append($('<td>').append(record.columnName))
            .append($('<td>').append(record.columnDescription))
          )
        })
        $table.append($('<tr>')
          .append($('<td>').append("..."))
          .append($('<td>').append("..."))
          .append($('<td>').append("..."))
        )
        $('#delCsvColumnDescription').prop('disabled', false);
      }
      else{
        $table.append($('<tr>')
          .append($('<td style="text-align: center; vertical-align: middle;" colspan="3">').append("no record found"))
        )
        $('#delCsvColumnDescription').prop('disabled', true);
      }

 		},
 		error: function(data) {
      console.log(data);
 		}
 	});

});

$("#csvTableLabelFile").change(function(){
  UploadCSV($(this), 'tableLabel.csv', $("#csvTableLabelTable"));
});

$("#csvTableDescriptionFile").change(function(){
  UploadCSV($(this), 'tableDescription.csv', $("#csvTableDescriptionTable"));
});

$("#csvColumnLabelFile").change(function(){
  UploadCSV($(this), 'columnLabel.csv', $("#csvColumnLabelTable"));
});

$("#csvColumnDescriptionFile").change(function(){
  UploadCSV($(this), 'columnDescription.csv', $("#csvColumnDescriptionTable"));
});

$("#csvRelationFile").change(function(){
  UploadCSV($(this), 'relation.csv', $("#csvRelationTable"));
});

$("#csvRelationExpFile").change(function(){
  UploadCSV($(this), 'relationExp.csv', $("#csvRelationExpTable"));
});

delCsvTableLabel.addEventListener('click', function(event){
  var $table = $("#csvTableLabelTable");
  $table.find("tr:gt(0)").remove();
  $table.append($('<tr>')
    .append($('<td style="text-align: center; vertical-align: middle;" colspan="7">').append("no record found"))
  )
  event.preventDefault();
}, false);

delCsvTableDescription.addEventListener('click', function(event){
  var $table = $("#csvTableDescriptionTable");
  $table.find("tr:gt(0)").remove();
  $table.append($('<tr>')
    .append($('<td style="text-align: center; vertical-align: middle;" colspan="7">').append("no record found"))
  )
  event.preventDefault();
}, false);

delCsvColumnLabel.addEventListener('click', function(event){
  var $table = $("#csvColumnLabelTable");
  $table.find("tr:gt(0)").remove();
  $table.append($('<tr>')
    .append($('<td style="text-align: center; vertical-align: middle;" colspan="7">').append("no record found"))
  )
  event.preventDefault();
}, false);

delCsvColumnDescription.addEventListener('click', function(event){
  var $table = $("#csvColumnDescriptionTable");
  $table.find("tr:gt(0)").remove();
  $table.append($('<tr>')
    .append($('<td style="text-align: center; vertical-align: middle;" colspan="7">').append("no record found"))
  )
  event.preventDefault();
}, false);

delCsvRelation.addEventListener('click', function(event){
  var $table = $("#csvRelationTable");
  $table.find("tr:gt(0)").remove();
  $table.append($('<tr>')
    .append($('<td style="text-align: center; vertical-align: middle;" colspan="7">').append("no record found"))
  )
  event.preventDefault();
}, false);

$('#delCsvTableLabel').click(function(){
  deleteCsv(['tableLabel.csv']);
})

$('#delCsvTableDescription').click(function(){
  deleteCsv(['tableDescription.csv']);
})

$('#delCsvColumnLabel').click(function(){
  deleteCsv(['columnLabel.csv']);
})

$('#delCsvTableDescription').click(function(){
  deleteCsv(['columnDescription.csv']);
})

$('#delCsvRelation').click(function(){
  deleteCsv(['relation.csv']);
})

function deleteCsv(files){

  var parms = {"files": files};
  console.log(parms);

  $.ajax({
 		type: 'POST',
 		url: "DeleteCSV",
 		dataType: 'json',
    data: JSON.stringify(parms),

 		success: function(data) {
      console.log(data)
      if(data.STATUS == 'OK'){
        var removed = "";
        if(data.REMOVED){
          removed = data.REMOVED.join("<br>");
          switch(parms.files[0]){
            case 'tableLabel.csv':
              $('#delCsvTableLabel').prop('disabled', true);
              break;
            case 'tableDescription.csv':
              $('#delCsvTableDescription').prop('disabled', true);
              break;
            case 'columnLabel.csv':
              $('#delCsvColumnLabel').prop('disabled', true);
              break;
            case 'columnDescription.csv':
              $('#delCsvColumnDescription').prop('disabled', true);
              break;
            case 'relation.csv':
              $('#delCsvRelation').prop('disabled', true);
              break;
          }
        }
        var alert = 'alert-info';
        var msg = "No change to save.";
        if(removed.trim().length > 1){
          alert = 'alert-success'
          msg = "Changed saved successfully.<br>Following file(s) removed:<br>" + removed;
        }
        switch(parms.files[0]){
          case 'relation.csv':
            ShowAlert(msg, alert, $("#csvRelationModalAlert"));
            break;
          default:
            ShowAlert(msg, alert, $("#csvLabelModalAlert"));
        }
      }
      else{
        switch(parms.files[0]){
          case 'relation.csv':
            ShowAlert(msg, alert, $("#csvRelationModalAlert"));
            ShowAlert(data.MESSAGE, "alert-danger", $("#csvRelationModalAlert"));
            break;
          default:
            ShowAlert(data.MESSAGE, "alert-danger", $("#csvLabelModalAlert"));
        }
      }

 		},
 		error: function(data) {
      console.log(data)
 		}
 	});
}

function UploadCSV($el, fileName, $table){

  var file = $el[0].files[0];
  console.log(file);

  var fd = new FormData();
  fd.append('file', file, fileName);
  console.log(fd);

  $.ajax({
    url: "UploadCSV",
    type: "POST",
    data: fd,
    enctype: 'multipart/form-data',
    processData: false,  // tell jQuery not to process the data
    contentType: false,   // tell jQuery not to set contentType
    success: function(data) {
      console.log(data);
      if(data.STATUS == "OK"){
        switch(fileName){
          case 'relation.csv':
            ShowAlert(data.MESSAGE, "alert-success", $("#csvRelationModalAlert"));
            break;
          case 'relationExp.csv':
            ShowAlert(data.MESSAGE, "alert-success", $("#csvRelationExpModalAlert"));
            break;
          default:
            ShowAlert(data.MESSAGE, "alert-success", $("#csvLabelModalAlert"));
        }
        $table.find("tr:gt(0)").remove();

        $.each(data.DATAS, function(i, record){
          switch(fileName){
              case 'tableLabel.csv':
                $table.append($('<tr>')
                  .append($('<td>').append(record.tableName))
                  .append($('<td>').append(record.tableLabel))
                )
                $('#delCsvTableLabel').prop('disabled', false);
                break;
              case 'tableDescription.csv':
                $table.append($('<tr>')
                  .append($('<td>').append(record.tableName))
                  .append($('<td>').append(record.tableDescription))
                )
                $('#delCsvTableDescription').prop('disabled', false);
                break;
              case 'columnLabel.csv':
                $table.append($('<tr>')
                  .append($('<td>').append(record.tableName))
                  .append($('<td>').append(record.columnName))
                  .append($('<td>').append(record.columnLabel))
                )
                $('#delCsvColumnLabel').prop('disabled', false);
                break;
              case 'columnDescription.csv':
                $table.append($('<tr>')
                .append($('<td>').append(record.tableName))
                .append($('<td>').append(record.columnName))
                .append($('<td>').append(record.columnDescription))
                )
                $('#delCsvColumnDescription').prop('disabled', false);
                break;
              case 'relation.csv':
                $table.append($('<tr>')
                .append($('<td>').append(record.FK_NAME))
                .append($('<td>').append(record.PK_NAME))
                .append($('<td>').append(record.FKTABLE_NAME))
                .append($('<td>').append(record.PKTABLE_NAME))
                .append($('<td>').append(record.KEY_SEQ))
                .append($('<td>').append(record.FKCOLUMN_NAME))
                .append($('<td>').append(record.PKCOLUMN_NAME))
                )
                $('#delCsvRelation').prop('disabled', false);
                break;
              case 'relationExp.csv':
                $table.append($('<tr>')
                .append($('<td>').append(record.JOIN_NAME))
                .append($('<td>').append(record.FKTABLE_NAME))
                .append($('<td>').append(record.PKTABLE_NAME))
                .append($('<td>').append(record.RELATION_EXPRESSION))
                )
                $('#delCsvRelationExp').prop('disabled', false);
                break;
            }
        })
        switch(fileName){
          case 'tableLabel.csv':
          case 'tableDescription.csv':
            $table.append($('<tr>')
              .append($('<td>').append("..."))
              .append($('<td>').append("..."))
            )
            break;
          case 'columnLabel.csv':
          case 'columnDescription.csv':
            $table.append($('<tr>')
              .append($('<td>').append("..."))
              .append($('<td>').append("..."))
              .append($('<td>').append("..."))
            )
            break;
          case 'relation.csv':
            $table.append($('<tr>')
              .append($('<td>').append("..."))
              .append($('<td>').append("..."))
              .append($('<td>').append("..."))
              .append($('<td>').append("..."))
              .append($('<td>').append("..."))
              .append($('<td>').append("..."))
              .append($('<td>').append("..."))
            )
            break;
            case 'relationExp.csv':
              $table.append($('<tr>')
                .append($('<td>').append("..."))
                .append($('<td>').append("..."))
                .append($('<td>').append("..."))
                .append($('<td>').append("..."))
              )
              break;
          }
      }
      else{
        switch(fileName){
          case 'relation.csv':
            ShowAlert(data.MESSAGE + "<br>" + data.TROUBLESHOOTING, "alert-danger", $("#csvRelationModalAlert"));
            break;
          default:
            ShowAlert(data.MESSAGE + "<br>" + data.TROUBLESHOOTING, "alert-danger", $("#csvLabelModalAlert"));
        }
        $table.find("tr:gt(0)").remove();
        $table.append($('<tr>')
          .append($('<td style="text-align: center; vertical-align: middle;" colspan="7">').append("no record found"))
        )

      }
		},
		error: function(data) {
      console.log(data);
		}
  }).done(function( data ) {
    console.log(data);
  });

  $el.val('');

}


$("#addSqlRel").click(function(){
  if(currentProject){
    console.log(currentProject.resource.jndiName);
    if(currentProject.resource.jndiName == "XML"){
      promptTeasing();  
    }
    else{
      $('#relsQueryModal').modal('toggle');
    }
  }
})

$('#relsQueryModal').on('shown.bs.modal', function() {

  if($('#searchSelect').val().length == 0){
    $('#searchSelect').selectpicker('selectAll');
  }

  $.ajax({
 		type: 'POST',
 		url: "GetRelationQuery",
 		dataType: 'json',

 		success: function(data) {
      console.log(data)
      if(data.DATAS){
        $("#FKQuery").val(data.DATAS.FKQuery);
        $("#PKQuery").val(data.DATAS.PKQuery);
      }
 		},
 		error: function(data) {
      console.log(data)
 			ShowAlert("Getting relations queries failed.", "alert-danger", $("#relsQueryModalAlert"));
 		}
 	});


});

$('#relsQueryModal').on('hidden.bs.modal', function() {
    $('#searchSelect').selectpicker('deselectAll');
    $('#relsQueryModal .collapse').each(function () {
        $(this).collapse('hide');
    });
    $("#FKQuery").val('');
    $("#PKQuery").val('');
});

function saveRelsQuery(){

  $('#relsQueryModal .collapse').each(function () {
      $(this).collapse('hide');
  });

  var FKQuery = $("#FKQuery").val().replace(/[^\x20-\x7E]/gmi, "");
  var PKQuery = $("#PKQuery").val().replace(/[^\x20-\x7E]/gmi, "");
  var parms = {"FKQuery" : FKQuery, "PKQuery": PKQuery};
  console.log(parms);

 	$.ajax({
 		type: 'POST',
 		url: "SaveRelationQuery",
 		dataType: 'json',
 		data: JSON.stringify(parms),

 		success: function(data) {
      console.log(data)
 			ShowAlert(data.MESSAGE, "alert-" + data.ALERT, $("#relsQueryModalAlert"));
      $('#searchSelect').selectpicker('unSelectAll');
 		},
 		error: function(data) {
      console.log(data)
 			ShowAlert("Saving Query failed.", "alert-danger", $("#relsQueryModalAlert"));
 		}
 	});

}

removeDBMDLabels.addEventListener('click', function(event){

  bootbox.confirm({
    title: "Removing labels From DBMD.",
    message: "DBMD labels and descriptions will be dropped. Do you confirm ?",
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Confirm',
          className: 'btn btn-warning'
      }
    },
    callback: function(result){
      if(result){
        $.ajax({
      		type: 'POST',
      		url: "RemoveDBMDLabels",
      		dataType: 'json',

      		success: function(data) {
            if(data.STATUS == "OK"){
              console.log(data);
              dbmd = data.DATAS;
              loadDBMD(dbmd);
            }
            else{
              showalert(data.FROM, "Removing DBMD labels failed.<br>" + data.MESSAGE + "<br>" + data.EXCEPTION, "alert-danger", "bottom")
            }
      		},
      		error: function(data) {
            console.log(data);
      		}
      	});

      }
    }
  });

  event.preventDefault();
}, false);

removeRel.addEventListener('click', function(event){

  bootbox.confirm({
    title: "Removing relations.",
    message: "Both SQL and CSV Relations will be dropped. Do you confirm ?",
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Confirm',
          className: 'btn btn-warning'
      }
    },
    callback: function(result){
      if(result){
        $.ajax({
      		type: 'POST',
      		url: "RemoveRelationQuery",
      		dataType: 'json',

      		success: function(data) {
            console.log(data);
            $("#FKQuery").val('');
            $("#PKQuery").val('');
            showalert("RemoveRelationQuery()", "Removing relations successfully.", "alert-success", "bottom");
      		},
      		error: function(data) {
            console.log(data);
      			showalert("RemoveRelationQuery()", "Removing relations query failed.", "alert-danger", "bottom");
      		}
      	});

      }
    }
  });

  event.preventDefault();
}, false);

runFKQuery.addEventListener('click', function(event){
  var query = $("#FKQuery").val().replace(/[^\x20-\x7E]/gmi, "");

  if(query.trim().length == 0){
    ShowAlert("Query is empty.", "alert-warning", $("#relsQueryModalAlert"));
    return;
  }
  var type = 'relation';
  var tables = $('#searchSelect').val();
  if(CheckIfTableSelected()){
    BuildTestQuery(query, type, tables);
  }

  event.preventDefault();
}, false);

eraseFKQuery.addEventListener('click', function(event){
  $("#FKQuery").val('');
  event.preventDefault();
}, false);

runPKQuery.addEventListener('click', function(event){
  var query = $("#PKQuery").val().replace(/[^\x20-\x7E]/gmi, "");
  if(query.trim().length == 0){
    ShowAlert("Query is empty.", "alert-warning", $("#relsQueryModalAlert"));
    return;
  }
  var type = 'relation';
  var tables = $('#searchSelect').val();
  if(CheckIfTableSelected()){
    BuildTestQuery(query, type, tables);
  }

  event.preventDefault();
}, false);

erasePKQuery.addEventListener('click', function(event){
  $("#PKQuery").val('');
  event.preventDefault();
}, false);

tableLabelQuery.addEventListener('click', function(event){
  var query = $("#tableLabel").val().replace(/[^\x20-\x7E]/gmi, "");
  if(query.trim().length == 0){
    ShowAlert("Query is empty.", "alert-warning", $("#tableLabelAlert"));
    return;
  }
  var type = 'table';
  var tables = $('#searchSelect').val();

  // var tables = new Set();
  //
  // if($datasTable.bootstrapTable("getData").length > 0){
  //   var qss = $datasTable.bootstrapTable("getData");
  //   $.each(qss, function(i, qs){
  //     tables.add(qs.table_name);
  //   })
  // }
  // tables = Array.from(tables);

  if(CheckIfTableSelected()){
    BuildTestQuery(query, type, tables);
  }
  event.preventDefault();
}, false);

eraseTableLabelQuery.addEventListener('click', function(event){
  $("#tableLabel").val('');
  event.preventDefault();
}, false);

// A MODIFIER dans myfunction.js
tableDescriptionQuery.addEventListener('click', function(event){
  var query = $("#tableDescription").val().replace(/[^\x20-\x7E]/gmi, "");
  if(query.trim().length == 0){
    ShowAlert("Query is empty.", "alert-warning", $("#tableDescriptionAlert"));
    return;
  }
  var type = 'table';
  var tables = $('#searchSelect').val();

  // var tables = new Set();
  //
  // if($datasTable.bootstrapTable("getData").length > 0){
  //   var qss = $datasTable.bootstrapTable("getData");
  //   $.each(qss, function(i, qs){
  //     tables.add(qs.table_name);
  //   })
  // }
  // tables = Array.from(tables);


  if(CheckIfTableSelected()){
    BuildTestQuery(query, type, tables);
  }
  event.preventDefault();
}, false);

eraseTableDescriptionQuery.addEventListener('click', function(event){
  $("#tableDescription").val('');
  event.preventDefault();
}, false);

columnLabelQuery.addEventListener('click', function(event){
  var query = $("#columnLabel").val().replace(/[^\x20-\x7E]/gmi, "");
  if(query.trim().length == 0){
    ShowAlert("Query is empty.", "alert-warning", $("#columnLabelAlert"));
    return;
  }
  var type = 'column';
  var tables = $('#searchSelect').val();

  // var tables = new Set();
  //
  // if($datasTable.bootstrapTable("getData").length > 0){
  //   var qss = $datasTable.bootstrapTable("getData");
  //   $.each(qss, function(i, qs){
  //     tables.add(qs.table_name);
  //   })
  // }
  // tables = Array.from(tables);

  if(CheckIfTableSelected()){
    BuildTestQuery(query, type, tables);
  }
  event.preventDefault();
}, false);

eraseColumnLabelQuery.addEventListener('click', function(event){
  $("#columnLabel").val('');
  event.preventDefault();
}, false);

columnDescriptionQuery.addEventListener('click', function(event){
  var query = $("#columnDescription").val().replace(/[^\x20-\x7E]/gmi, "");
  if(query.trim().length == 0){
    ShowAlert("Query is empty.", "alert-warning", $("#columnDescriptionAlert"));
    return;
  }
  var type = 'column';
  var tables = $('#searchSelect').val();

  // var tables = new Set();
  //
  // if($datasTable.bootstrapTable("getData").length > 0){
  //   var qss = $datasTable.bootstrapTable("getData");
  //   $.each(qss, function(i, qs){
  //     tables.add(qs.table_name);
  //   })
  // }
  // tables = Array.from(tables);

  if(CheckIfTableSelected()){
    BuildTestQuery(query, type, tables);
  }
  event.preventDefault();
}, false);

eraseColumnDescriptionQuery.addEventListener('click', function(event){
  $("#columnDescription").val('');
  event.preventDefault();
}, false);

// END

function openDynamicModal(){
  $('#dynamicModal').modal('toggle');
}

function CheckIfTableSelected(){
  if($('#searchSelect').val().length == 0){
    showalert("CheckIfTableSelected()", "No table selected in 'Choose table(s)...'.", "alert-warning", "bottom");
    return false;
    // $('#searchSelect').selectpicker('selectAll');
  }
  return true;
}

function BuildTestQuery(query, type, tables){

  if(tables.length > 20){
    tables = tables.slice(0, 20);
  }

  var parms = {query: query, type: type, tables: tables};

  console.log(parms);

  $.ajax({
		type: 'POST',
		url: "BuildTestQuery",
		dataType: 'json',
    data: JSON.stringify(parms),

		success: function(query) {
      console.log(query);
      WatchContent(query.query);
		},
		error: function(data) {
			showalert("BuildTestQuery()", "Getting test query failed.", "alert-danger", "bottom");
		}
	});

}

function WatchContent(query){
  // var query = "select * from " + table;
  console.log(query);
  var parms = {query: query};
  localStorage.setItem('SQLQuery', JSON.stringify(parms));
  window.open("watchContent.html");
}

function OpenQueryModal(){
  $('#queryModal').modal('toggle');
  // GetLabelsQueries();
}

function OpenQueries(id){

  var queriesName;

  $.each(queriesList, function(i, obj){
    if(obj.id == id){
      queriesName = obj.name;
    }
  });

  console.log("queriesName=" + queriesName);

	$.ajax({
		type: 'POST',
		url: "OpenQueries",
		dataType: 'json',
    data: "queries=" + queriesName,

		success: function(queries) {
      console.log(queries);
      $('#tableLabel').val(queries.tlQuery);
      $('#tableDescription').val(queries.tdQuery);
      $('#columnLabel').val(queries.clQuery);
      $('#columnDescription').val(queries.cdQuery);
      // showalert("OpenQueries()", "Queries opened successfully.", "alert-success", "bottom");

		},
		error: function(data) {
			showalert("OpenQueries()", "Opening queries failed.", "alert-danger", "bottom");
		}
	});

  $('#modQueriesList').modal('toggle');

}


function GetQueriesList(){

  $('#queryModal .collapse').each(function () {
      $(this).collapse('hide');
  });

	$.ajax({
		type: 'POST',
		url: "GetQueriesList",
		dataType: 'json',

		success: function(data) {
      queriesList = data;
      data.sort(function(a, b) {
        return b - a;
      });
      console.log("queriesList");
      console.log(queriesList);
			// showalert("GetQueriesList()", "Queries list get successfull.", "alert-success", "bottom");
      $('#modQueriesList').modal('toggle');
		},
		error: function(data) {
			ShowAlert("Getting queries list failed.", "alert-danger", $("#queryModalAlert"));
		}
	});


}

function SaveQueries(){

  $('#queryModal .collapse').each(function () {
      $(this).collapse('hide');
  });

  var backupName;

  var vide = true;

  if($("#tableLabel").val().trim().length > 1) {
    vide = false;
  }
  if($("#tableDescription").val().trim().length > 1) {
    vide = false;
  }
  if($("#columnLabel").val().trim().length > 1) {
    vide = false;
  }
  if($("#columnDescription").val().trim().length > 1) {
    vide = false;
  }

  console.log(vide);

  if(vide){
    ShowAlert("Nothing to save.", "alert-warning", $("#queryModalAlert"));
    return;
  }


  bootbox.prompt({
    size: "small",
    title: "Enter backup name",
    callback: function(result){
       /* result = String containing user input if OK clicked or null if Cancel clicked */
      backupName = result;
      if(!backupName){
        return;
      }

      var queries = {};
      queries.tlQuery = $('#tableLabel').val();
      queries.tdQuery = $('#tableDescription').val();
      queries.clQuery = $('#columnLabel').val();
      queries.cdQuery = $('#columnDescription').val();

      var parms = {backupName: backupName, data: queries};

     	$.ajax({
     		type: 'POST',
     		url: "SaveQueries",
     		dataType: 'json',
     		data: JSON.stringify(parms),

     		success: function(data) {
     			ShowAlert("Queries saved successfully.", "alert-success", $("#queryModalAlert"));
     		},
     		error: function(data) {
     			ShowAlert("Saving Queries failed.", "alert-danger", $("#queryModalAlert"));
     		}
     	});
    }
  });

}


function GetLabelsQueries(){

  $.ajax({
    type: 'POST',
    url: "GetLabelsQueries",
    dataType: 'json',

    success: function(queries) {
      console.log(queries);
      $('#tableLabel').val(queries.tlQuery);
      $('#tableDescription').val(queries.tdQuery);
      $('#columnLabel').val(queries.clQuery);
      $('#columnDescription').val(queries.cdQuery);
    },
    error: function(data) {
      console.log(data);
    }
  });

}

function GetCsvLabels(){
  $('#csvLabelModal .collapse').each(function () {
      $(this).collapse('hide');
  });

  if(!CheckIfTableSelected()){
    return;
  }

  var parms = {};
  parms.tables = $('#searchSelect').val();
  console.log(JSON.stringify(parms));

  $.ajax({
    type: 'POST',
    url: "GetDBMDCsvLabels",
    dataType: 'json',
    data: JSON.stringify(parms),

    success: function(labels) {
      console.log(labels);
      if(labels.STATUS == "KO"){
        ShowAlert("ERROR: " + labels.MESSAGE + "<br>TROUBLESHOOTING: " + labels.TROUBLESHOOTING, "alert-danger", $("#csvLabelModalAlert"));
      }
      else{
        dbmd = labels.DATAS;
        console.log(Object.values(dbmd));
        loadDBMD(Object.values(dbmd));

        $('#csvLabelModal').modal('toggle');
      }

    },
    error: function(data) {
      console.log(data);
    }
  });
}

function GetLabels(){
  // $('#queryModal .collapse').each(function () {
  //     $(this).collapse('hide');
  // });

  if(!CheckIfTableSelected()){
    return;
  }

  var parms = {};
  parms.tables = $('#searchSelect').val();
  parms.tlQuery = $('#tableLabel').val();
  parms.tdQuery = $('#tableDescription').val();
  parms.clQuery = $('#columnLabel').val();
  parms.cdQuery = $('#columnDescription').val();

  console.log(parms);
  // console.log(JSON.stringify(parms));

  // $('#queryModal').modal('toggle');

  $.ajax({
    type: 'POST',
    url: "GetLabels",
    dataType: 'json',
    data: JSON.stringify(parms),

    success: function(labels) {
      console.log(labels);
      if(labels.STATUS == "KO"){
        // $('#queryModal').modal('toggle');
        ShowAlert("ERROR: " + labels.MESSAGE + "<br>TROUBLESHOOTING: " + labels.TROUBLESHOOTING, "alert-danger", $("#queryModalAlert"));
      }
      else{
        dbmd = labels.DATAS;
        console.log(Object.values(dbmd));
        loadDBMD(Object.values(dbmd));
        // console.log(dbmd);
      }

    },
    error: function(data) {
      console.log(data);
    }
  });

  // $('#queryModal').modal('toggle');

}

function Filter(){
  var selection = $('#searchSelect').val();
  console.log(selection);

  console.log($('#searchTable').bootstrapTable("getOptions").strictSearch);
  $('#searchTable').bootstrapTable("filterBy", {});
  $('#searchTable').bootstrapTable("filterBy", {table_name: selection});
  var datas = $('#searchTable').bootstrapTable("getData");
  console.log(datas);

  $.each(datas, function(i, row){
    row.filtered = true;
    $.each(selection, function(j, table){
      if(row.table_name.match(table)){
        row.filtered = false;
        return false;
      }
    });
  });

  selection = $('#searchColumnSelect').val();
  console.log(selection);
  var cols = $('#searchTable').bootstrapTable('getOptions').columns[0];
  console.log(cols);

  $.each(cols, function(i, col){
    col.searchable = false;
    $.each(selection, function(j, colName){
      if(col.field.match(colName)){
        col.searchable = true;
        return false;
      }
    });
  });

  $('#searchTable').bootstrapTable('destroy');
  buildTable($('#searchTable'), cols);
  $('#searchTable').bootstrapTable('load', datas);
  $('#searchTable').bootstrapTable("filterBy", {filtered: false});

}

function checkDBMD(){

  $.ajax({
    type: 'POST',
    url: "GetDBMDFromCache",
    dataType: 'json',
    async: true,
    success: function(data) {
      console.log(data);
      if(data.DATAS && data.DATAS != null && !jQuery.isEmptyObject(data.DATAS)){
        if(Object.keys(data.DATAS).length > 0){
          dbmd = Object.values(data.DATAS);
          loadDBMD(dbmd);
        }
      }
      else{
          bootbox.confirm({
            title: "No database metadata found in cache.",
            message: "You have to load database metadata to use this tool. This could take few minutes.",
            buttons: {
              cancel: {
                  label: 'Cancel',
                  className: 'btn btn-default'
              },
              confirm: {
                  label: 'Confirm',
                  className: 'btn btn-primary'
              }
            },
            callback: function (result) {
                console.log(result);
                if(result){
                    GetDBMD($('#searchTable'));
                }
            }
          });
      }

    },
    error: function(data) {
      console.log(data);
    }

  });
}

function GetDBMD(table) {

    $.when(
      $.ajax({
        type: 'POST',
        url: "GetDBMD",
        dataType: 'json',
        async: true,
        success: function(data) {
          // dbmd = data;
          console.log(data);
          bootbox.confirm({
            title: "Save DBMD.",
            message: "Do you want to save DBMD ?",
            buttons: {
              cancel: {
                  label: 'Cancel',
                  className: 'btn btn-default'
              },
              confirm: {
                  label: 'Save',
                  className: 'btn btn-primary'
              }
            },
            callback: function (result) {
                console.log(result);
                if(result){
                  SaveDBMD();
                }
            }
          });
        }
      })
    )
    .then(
      function(data){
        dbmd = data.DATAS;
        loadDBMD(dbmd);
      }
    );

}

function loadDBMD(dbmd){

  var datas = [];

  $.each(dbmd, function(i, table){
    $.each(table.columns, function(j, column){
      var field = {};
      field.table_name = table.table_name;
      field.table_type = table.table_type;
      field.table_remarks = table.table_remarks;
      field.table_description = table.table_description;
      field.table_recCount = table.table_recCount;
      field.table_fieldCount = table.table_fieldCount;
      field.table_importedKeysCount = table.table_importedKeysCount;
      field.table_exportedKeysCount = table.table_exportedKeysCount;
      field.table_importedKeysSeqCount = table.table_importedKeysSeqCount;
      field.table_exportedKeysSeqCount = table.table_exportedKeysSeqCount;
      field.table_primaryKeyFieldsCount = table.table_primaryKeyFieldsCount;
      field.table_stats = table.table_stats;
      field.table_indexesCount = table.table_indexesCount;
      field.column_name = column.column_name;
      field.column_type = column.column_type;
      field.column_remarks = column.column_remarks;
      field.column_description = column.column_description;
      field.column_isPrimaryKey = column.column_isPrimaryKey;
      field.column_isIndexed = column.column_isIndexed;
      field.column_size = column.column_size;
      field.column_isNullable = column.column_isNullable;
      field.filtered = column.filtered;
      datas.push(field);
    })
  });

  $('#searchTable').bootstrapTable("load", datas);
  ChooseTable($('#searchSelect'), '2');

}

function SaveDBMD(){

    $.ajax({
      type: 'POST',
      url: "SaveDBMD",
      dataType: 'json',
      data: JSON.stringify(dbmd),

      success: function(data) {
        showalert("SaveDBMD()", "Database metadata saved successfully.", "alert-success", "bottom");

      },
      error: function(data) {
        // showalert("SaveDBMD()", "Saving database metadata failed.", "alert-danger", "bottom");
        showalert("SaveDBMD()", "Database metadata saved successfully.", "alert-success", "bottom");
      }
    });

}

function RemoveDBMD(){

  bootbox.confirm({
    title: "Remove DBMD.",
    message: "Do you want to remove DBMD ?",
    buttons: {
      cancel: {
          label: 'Cancel',
          className: 'btn btn-default'
      },
      confirm: {
          label: 'Confirm',
          className: 'btn btn-primary'
      }
    },
    callback: function (result) {
        console.log(result);
        if(result){
          $.ajax({
            type: 'POST',
            url: "RemoveDBMD",
            dataType: 'json',
        
            success: function(data) {
              console.log(data);
              alert = "alert-success";
              if(data.STATUS == "OK"){
                showalert("", data.MESSAGE, alert, "bottom");
                $('#searchTable').bootstrapTable("removeAll");
                $('#searchSelect').empty();
                $('#searchSelect').selectpicker('refresh');
                ChooseTable($('#searchSelect'));
                checkDBMD();
              }
              else{
                alert = "alert-warning";
                showalert("", data.MESSAGE, alert, "bottom");
              }
        
            },
            error: function(data) {
              // showalert("RemoveDBMD()", "Database metadata file was not deleted or does not exists.", "alert-warning", "bottom");
            }
          });
        }
    }
  });

}


function buildTable($el, cols) {

    $el.bootstrapTable({
        columns: cols,
        // url: url,
        // data: data,
        toolbar: $('#searchToolbar'),
        search: true,
        searchOnEnterKey: true,
        showRefresh: false,
        showColumns: true,
        showToggle: false,
        pagination: true,
        pageSize: 25,
        showPaginationSwitch: true,
        paginationVAlign: "both",
        detailView: false
    })

    var $tableHeaders = $el.find('thead > tr > th');
    console.log('$tableHeaders');
    console.log($tableHeaders.eq(3));
    var cols = $el.bootstrapTable('getOptions').columns[0];
    console.log(cols);
    $.each(cols, function(i, col){
      if(col.title.match('Table Type')){
        col.searchable = false;
      };
    });
    console.log(cols);
}

function ChooseColumn(table){
  table.empty();

  $.each($('#searchTable').bootstrapTable('getOptions').columns[0], function(i, obj){
    if(!obj.title.match("glyphicon-question-sign")){
      var option = '<option class="fontsize" value=' + obj.field + '>' + obj.title + '</option>';
      table.append(option);
    }
  });
  table.selectpicker('refresh');
}

function SortOnStats(){

  bootbox.prompt({
      title: "Sort tables list.",
      inputType: 'select',
      buttons: {
        cancel: {
            label: 'Cancel',
            className: 'btn btn-default'
        },
        confirm: {
            label: 'Confirm',
            className: 'btn btn-primary'
        }
      },
      inputOptions: [
          {
              text: 'Sort by...',
              value: '',
          },
          {
              text: '...alphabetic table name (ASC).',
              value: '0',
          },
          {
              text: '...number of fields within primary key (DESC).',
              value: '1',
          },
          {
              text: '...number of primary keys imported (DESC).',
              value: '2',
          },
          {
              text: '...number of sequence within primary keys imported (DESC).',
              value: '3',
          },
          {
              text: '...number of foreign keys exported (DESC).',
              value: '4',
          },
          {
              text: '...number of sequence within foreign keys exported (DESC).',
              value: '5',
          },
          {
              text: '...number of indexed fields (DESC).',
              value: '6',
          },
          {
              text: '...number of records (DESC).',
              value: '7',
          },
          {
            text: '...number of fields (DESC).',
            value: '8',
        }

      ],
      callback: function (result) {
          console.log(result);
          ChooseTable($('#searchSelect'), result);
          console.log(result);
      }
  });

}

function ChooseTable(table, sort) {

    table.empty();


    if($('#searchTable').bootstrapTable('getData').length > 0) {
      console.log("dbmd loaded from cache...");
      console.log(dbmd);
      var tables = Object.values(dbmd);

      console.log(tables);
      console.log(tables);
      if(!sort){
        sort = "3";
        console.log('Default to sort ' + sort);
      }

      switch(sort){
        case "0":
          tables.sort(function(a, b){
            return a.table_name.localeCompare(b.table_name);
          });
          break;
        case "1":
          tables.sort(function(a, b){return b.table_primaryKeyFieldsCount - a.table_primaryKeyFieldsCount});
          break;
        case "2":
          tables.sort(function(a, b){return b.table_importedKeysCount - a.table_importedKeysCount});
          break;
        case "3":
          tables.sort(function(a, b){return b.table_importedKeysSeqCount - a.table_importedKeysSeqCount});
          break;
        case "4":
          tables.sort(function(a, b){return b.table_exportedKeysCount - a.table_exportedKeysCount});
          break;
        case "5":
          tables.sort(function(a, b){return b.table_exportedKeysSeqCount - a.table_exportedKeysSeqCount});
          break;
        case "6":
          tables.sort(function(a, b){return b.table_indexesCount - a.table_indexesCount});
          break;
        case "7":
          tables.sort(function(a, b){return b.table_recCount - a.table_recCount});
          break;
        case "8":
          tables.sort(function(a, b){return b.table_fieldCount - a.table_fieldCount});
          break;
        default:
          tables.sort(function(a, b){return b.table_primaryKeyFieldsCount - a.table_primaryKeyFieldsCount});
      }

      $.each(tables, function(i, obj){
        var option = "";
        if(obj.table_remarks == null){obj.table_remarks = ""};
        if(obj.table_type == "VIEW"){
          option = '<option class="fontsize" value="' + obj.table_name + '" data-subtext="(' + obj.table_type + ') ' + obj.table_remarks +  ' ' + obj.table_stats + '">' + obj.table_name + '</option>';
        }
        if(obj.table_type == "TABLE"){
          option = '<option class="fontsize" value="' + obj.table_name + '" data-subtext="' + obj.table_remarks +  ' ' + obj.table_stats + '">' + obj.table_name + '</option>';
        }
        table.append(option);
        // $('#modPKTables').append(option);
        // table.append('<option class="fontsize" value=' + obj.name + '>' + obj.name + '</option>');
      });
      table.selectpicker('refresh');
      // $('#modPKTables').selectpicker('refresh');
      // localStorage.setItem('tables', JSON.stringify(tables));
    }
    else {
      $.ajax({
      type: 'POST',
      url: "GetTables",
      dataType: 'json',
      async: true,
      // success: function(tables) {
      //   console.log(tables);
      //   $.each(tables, function(i, obj){
      //     var option = '<option class="fontsize" value="' + obj.name + '">' + obj.name + '</option>';
      //     table.append(option);
      //   });
      //   table.selectpicker('refresh');
      // }
      success: function(data) {
        console.log(data);
        $.each(data.TABLES, function(tableName, tableType){
          if(tableType == "VIEW"){
            var option = '<option class="fontsize" value="' + tableName + '" data-subtext="(' + tableType + ')">' + tableName + '</option>';
          }
          if(tableType == "TABLE"){
            var option = '<option class="fontsize" value="' + tableName + '">' + tableName + '</option>';
          }
          table.append(option);
        });
        table.selectpicker('refresh');
      },
      error: function(data) {
          console.log(data);
      }

      });
    }

}

function ShowAlert(message, alertType, $el) {

    $('#alertmsg').remove();

    var timeout = 3000;

    if(alertType.match('alert-warning')){
      timeout = 10000;
    }
    if(alertType.match('alert-danger')){
      timeout = 15000;
    }

    var $newDiv;

    if(alertType.match('alert-success|alert-info')){
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<p>' +
          message +
          '</p>'
        )
       .addClass('alert ' + alertType);
    }
    else{
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
          '<p>' +
          '<strong>' + message + '</strong>' +
          '</p>'
        )
       .addClass('alert ' + alertType + ' alert-dismissible');
    }

    if($el){
      $el.append($newDiv);
    }
    else{
      $('#Alert').append($newDiv);
    }

    setTimeout(function() {
       $('#alertmsg').remove();
    }, timeout);

}

function showalert(title, message, alertType, area) {

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
          '<h4>' + title + '</h4>' +
          '<p>' +
          message +
          '</p>'
        )
       .addClass('alert ' + alertType + ' flyover flyover-' + area);
    }
    else{
      $newDiv = $('<div/>')
       .attr( 'id', 'alertmsg' )
       .html(
          '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
          '<h4>' + title + '</h4>' +
          '<p>' +
          '<strong>' + message + '</strong>' +
          '</p>'
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

function enableUser(enableBtn) {
    let card = $(enableBtn).closest('.card');
    let userid = card.attr('data');
    
    $.ajax({
        url: "/admin/user/" + userid + "/enable",
        type: 'POST',
        beforeSend: function(request) {
            request.setRequestHeader($("meta[name='_csrf_header']").attr("content"),
                                     $("meta[name='_csrf']").attr("content"));
         },
        success: (result) => {
            if ((typeof result === 'object') && (result !== null)) {
                // If the user was sucessfully enabled, remove the card
                if (result.status === 'enabled') {
                    card.fadeOut("normal", function() {
                        card.remove();
                    });
                }
                else {
                    $(card.find('.card-body')).append("<div>" + result.status + "</div>");
                }
            }
        }
    });
}

function deleteUser(deleteBtn) {
    let card = $(deleteBtn).closest('.card');
    let userid = card.attr('data');
    
    let really = confirm("Do you really want to delete " + userid + "?");
    
    if (!really) {
        return;
    }
    
    $.ajax({
        url: "/admin/user/" + userid,
        type: 'DELETE',
        beforeSend: function(request) {
            request.setRequestHeader($("meta[name='_csrf_header']").attr("content"),
                                     $("meta[name='_csrf']").attr("content"));
         },
        success: (result) => {
            if ((typeof result === 'object') && (result !== null)) {
                // If the user was sucessfully deleted, remove the card
                if (result.status === 'deleted') {
                    card.fadeOut("normal", function() {
                        card.remove();
                    });
                }
                else {
                    $(card.find('.card-body')).append("<div>" + result.status + "</div>");
                }
            }
        }
    });
}
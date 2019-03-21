/**
 * Show the item creation form and hide the activation button
 * 
 * @param {element} button the button that activates the form display
 */
function newNamedItem(button) {
    // find the container for the button and the form
    button = $(button);
    let itemContainer = button.closest('.newItem');
    let form = $(itemContainer.find('.newItemForm'));
    
    // Show the form, then replace the activation button with the submit button
    form.show(500, "linear", () => {
        $(button).hide(); 
        $(form.find('[type=submit]')).show();
    }).css("display", "inline-block");
}

/**
 * Validate creation of a new course -- that a name has been specified
 * 
 * @param {Element:form} form the form being submitted
 * @returns {boolean} true if valid, false if not
 */
function checkCourseCreate(form) {
    form = $(form);
    let courseNameInput = $(form.find("[name='courseName']"));
    
    if (!courseNameInput.val()) {
        alert("Courses need to have a name.");
        courseNameInput.addClass('has-error');
        return false;
    }
    
    return true;
}


/**
 * Validate creation of a new assignment -- that a name has been specified
 * 
 * @param {Element:form} form the form being submitted
 * @returns {boolean} true if valid, false if not
 */
function checkAssignmentCreate(form) {
    form = $(form);
    let assignmentNameInput = $(form.find("[name='assignmentName']"));
    
    if (!assignmentNameInput.val()) {
        alert("Assignments need to have a name.");
        assignmentNameInput.addClass('has-error');
        return false;
    }
    
    return true;
}

/**
 * Create a random v4 UUID 
 * @returns {String} the UUID
 */
function generateUUID() { // Public Domain/MIT
    var d = new Date().getTime();
    if (typeof performance !== 'undefined' && typeof performance.now === 'function'){
        d += performance.now(); //use high-precision timer if available
    }
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
}

/**
 * Generate a new shared secret for a course
 * @param {element} button the button clicked to generate a shared secret 
 * @returns false
 */
function newSecret(button) {
    let doIt = confirm("This will replace your course's shared secret." +
            " You will need to update your LMS to use the new value.");

    if (doIt) {
        button = $(button);
        
        let container = button.closest(".input-group");
        let input = $(container.find("input[name=courseSecret]"));
        
        input.val(generateUUID());
    }
    
    return false;    
}

/**
 * Copy the input in the same input group as a (copy) button
 * @param {element} button
 * @returns false
 */
function copyInput(button) {
    // Find the text input in the same group as the button
    button = $(button);
    let container = button.closest(".input-group");
    let input = $(container.find("input[type=text]"));
    
    // Select the text field
    input.select();
    
    // Copy the text inside the text field
    document.execCommand("copy");
    
    return false;
}

function deleteCourse() {
    let doIt = confirm("Do you really want to delete this course?");
    
    if (!doIt) {
        return false;
    }
    
    $.ajax({
        url: './',
        type: 'DELETE',
        beforeSend: function(request) {
            request.setRequestHeader($("meta[name='_csrf_header']").attr("content"),
                                     $("meta[name='_csrf']").attr("content"));
         }
    }).done(() => {
        window.location.href = '/';
    }).fail((result) => {
        alert("Could not delete the course: " + result.status + " " + result.statusText);
    });
}
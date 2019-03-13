/**
 * Show the course creation form and hide the activation button
 * 
 * @param {element} button the button that activates the form display
 */
function newCourse(button) {
    // find the container for the button and the form
    button = $(button);
    let itemContainer = button.closest('.courseItem');
    let form = $(itemContainer.find('.newCourseForm'));
    
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

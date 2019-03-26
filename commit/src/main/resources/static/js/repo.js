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

$('.dateTime').each((i, d)=>{$(d).text(new Date($(d).text()))});
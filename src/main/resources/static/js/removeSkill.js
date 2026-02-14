function removeSkill(button) {

    const skillDiv = button.parentElement;

    skillDiv.remove();

    updateSkillIndexes();
    
}
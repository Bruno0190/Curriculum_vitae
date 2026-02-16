function addSkill() {

    const skillsContainer = document.getElementById('skills_container');

    const currentCount = skillsContainer.querySelectorAll('.div_skill').length;

    const newSkillDiv = document.createElement('div');
    newSkillDiv.classList.add('div_skill'); 
    
    newSkillDiv.innerHTML = `
        <input type="text" class="form-control" placeholder="Skill" name="skills[${currentCount}].skill_name">
        <button type="button" class="btn btn-sm btn-danger mt-1" onclick="removeSkill(this)">Rimuovi</button>
    `;
    skillsContainer.appendChild(newSkillDiv);

    updateSkillIndexes();

}
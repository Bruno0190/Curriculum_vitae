function updateSkillIndexes() {

    const skillDivs = document.querySelectorAll('#skills-container .div_skill');

    for (let i = 0; i < skillDivs.length; i++) {

        const div = skillDivs[i];

        const input = div.querySelector('input');

        input.name = `skills[${i}].skill_name`;

    }
}
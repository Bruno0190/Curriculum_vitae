let profileInput = document.getElementById(`profile_image_area`);

let certificateInputs = document.querySelectorAll('.certificate_image_input');
let pendingUploads = 0;

function setSaveDisabled(disabled) {
    const saveButtons = document.querySelectorAll("button[type='submit'][form='row_cv'], #row_cv button[type='submit']");
    saveButtons.forEach(btn => {
        btn.disabled = disabled;
    });
}

function beginUpload() {
    pendingUploads += 1;
    setSaveDisabled(true);
}

function endUpload() {
    pendingUploads = Math.max(0, pendingUploads - 1);
    if (pendingUploads === 0) {
        setSaveDisabled(false);
    }
}

function uploadFromInput(inputElement) {
    if (!inputElement || !inputElement.files || inputElement.files.length === 0) {
        return;
    }

    let file = inputElement.files[0];
    if (inputElement === profileInput) {
        const profileImg = document.querySelector("#FOTO img");
        if (profileImg) {
            profileImg.src = URL.createObjectURL(file);
        }
        let urlInput = document.getElementById('profileImageUrl');
        uploadFile(file, "profiles", urlInput);
        return;
    }

    if (inputElement.classList.contains('certificate_image_input')) {
        let urlInput = inputElement.parentElement.querySelector('.certificate_url_input');
        uploadFile(file, "certificates", urlInput);
    }
}

function dragAndDropFile(event, inputElement) {

    event.preventDefault();

    let files = event.dataTransfer.files;

    if (files.length > 0) {
        
        let file = files[0]
        let uploaded = new DataTransfer();
        uploaded.items.add(file);
        inputElement.files = uploaded.files;

        if (inputElement === profileInput) {

            let urlInput = document.getElementById('profileImageUrl');
            uploadFile(file, "profiles", urlInput);
            return;
        } else if (inputElement.classList.contains('certificate_image_input')) {

            let urlInput = inputElement.parentElement.querySelector('.certificate_url_input');
            uploadFile(file, "certificates", urlInput);
            return;
        }

    }

}

if (profileInput) {
    profileInput.addEventListener("dragover", event => event.preventDefault());
    profileInput.addEventListener("drop", event => dragAndDropFile(event, profileInput));
    profileInput.addEventListener("change", () => uploadFromInput(profileInput));
}

certificateInputs.forEach(input => {
    input.addEventListener("dragover", event => event.preventDefault());
    input.addEventListener("drop", event => dragAndDropFile(event, input));
    input.addEventListener("change", () => uploadFromInput(input));
});
    
async function uploadFile(file, folder, urlInput) {
    const formData = new FormData();
    formData.append("file", file);

    beginUpload();
    try {
        const response = await fetch(`/upload/${folder}`, {
            method: "POST",
            body: formData
        });
    
        if (response.ok) {
            const url = await response.text();
            urlInput.value = url;
        } else {
            const message = await response.text();
            alert(message || "Errore durante upload file.");
        }
    } catch (error) {
        console.error("Error uploading file:", error);
        alert("Errore di rete durante upload file.");
    } finally {
        endUpload();
    }
} 

const rowForm = document.getElementById("row_cv");
if (rowForm) {
    rowForm.addEventListener("submit", function (event) {
        if (pendingUploads > 0) {
            event.preventDefault();
            alert("Attendi il completamento dell'upload immagine prima di salvare.");
        }
    });
}


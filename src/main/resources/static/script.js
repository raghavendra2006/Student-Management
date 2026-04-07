async function submitStudent() {

    const student = {
        name: document.getElementById("name").value,
        rollNumber: document.getElementById("rollNumber").value,
        branch: document.getElementById("branch").value,
        college: document.getElementById("college").value,
        skills: document.getElementById("skills").value,
        email: document.getElementById("email").value,
        phone: document.getElementById("phone").value,
        age: document.getElementById("age").value,
        gender: document.getElementById("gender").value
    };

    const file = document.getElementById("file").files[0];

    const formData = new FormData();
    formData.append("student", JSON.stringify(student));

    if (file) {
        formData.append("file", file);
    }

    let url = "/students";
    let method = "POST";

    if (editId) {
        url = `/students/${editId}`;
        method = "PUT";
    }

    try {
        const res = await fetch(url, { method, body: formData });

        const text = await res.text(); // 🔥 get actual error

        if (res.ok) {
            alert(editId ? "Updated ✅" : "Added ✅");
            closeModal();
            loadStudents();
        } else {
            console.error("ERROR:", text);
            alert("Failed: " + text); // 🔥 real error shown
        }

    } catch (err) {
        console.error("EXCEPTION:", err);
        alert("Server error ❌");
    }
}

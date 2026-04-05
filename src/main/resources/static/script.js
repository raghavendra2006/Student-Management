const BASE_URL = "/auth";

/* ================= AUTH ================= */

async function register() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const res = await fetch(`${BASE_URL}/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password, role: "USER" })
    });

    if (res.ok) {
        alert("Signup successful ✅");
        location.href = "/login";
    } else {
        alert("Signup failed ❌");
    }
}

async function login() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const res = await fetch(`${BASE_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    });

    if (res.ok) {
        const token = await res.text();
        localStorage.setItem("token", token);
        location.href = "/dashboard";
    } else {
        alert("Invalid credentials ❌");
    }
}

function logout() {
    localStorage.removeItem("token");
    location.href = "/login";
}

/* ================= STUDENTS ================= */

let page = 0;
let sortType = "";
let editId = null;

/* LOAD */
async function loadStudents() {
    let url = `/students/filter?page=${page}&size=4`;

    if (sortType === "skills") {
        url = `/students/filter?sort=skills`;
    } else if (sortType) {
        url += `&sort=${sortType}`;
    }

    const res = await fetch(url);
    const data = await res.json();

    renderTable(data.content);
}

/* TABLE */
function renderTable(list) {
    const table = document.getElementById("studentTable");
    table.innerHTML = "";

    list.forEach(s => {
        table.innerHTML += `
        <tr>
            <td><img src="${s.imageUrl}" /></td>
            <td>${s.name}</td>
            <td>${s.rollNumber}</td>
            <td>${s.branch}</td>
            <td>${s.skills}</td>
            <td>${s.age}</td>
            <td>${s.gender}</td>
            <td class="action-buttons">
    <button class="edit-btn" onclick="editStudent(${s.id})">
        ✏ Edit
    </button>

    <button class="delete-btn" onclick="deleteStudent(${s.id})">
        🗑 Delete
    </button>
</td>
        </tr>`;
    });
}

/* PAGINATION */
function nextPage() {
    page++;
    loadStudents();
}

function prevPage() {
    if (page > 0) page--;
    loadStudents();
}

/* SORT */
function applySort() {
    sortType = document.getElementById("sort").value;
    page = 0;
    loadStudents();
}

/* MODAL */
function openModal() {
    document.getElementById("modal").style.display = "block";
}

function closeModal() {
    document.getElementById("modal").style.display = "none";
    resetForm();
    editId = null;
}

/* EDIT */
async function editStudent(id) {
    editId = id;

    const res = await fetch(`/students/${id}`);
    const s = await res.json();

    document.getElementById("name").value = s.name;
    document.getElementById("rollNumber").value = s.rollNumber;
    document.getElementById("branch").value = s.branch;
    document.getElementById("college").value = s.college;
    document.getElementById("skills").value = s.skills;
    document.getElementById("email").value = s.email;
    document.getElementById("phone").value = s.phone;
    document.getElementById("age").value = s.age;
    document.getElementById("gender").value = s.gender;

    document.getElementById("modalTitle").innerText = "Edit Student";
    openModal();
}

/* ADD / UPDATE */
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
    if (file) formData.append("file", file);

    let url = "/students";
    let method = "POST";

    if (editId) {
        url = `/students/${editId}`;
        method = "PUT";
    }

    const res = await fetch(url, { method, body: formData });

    if (res.ok) {
        alert(editId ? "Updated ✅" : "Added ✅");
        closeModal();
        loadStudents();
    } else {
        alert("Operation failed ❌");
    }
}

/* DELETE */
async function deleteStudent(id) {
    await fetch(`/students/${id}`, { method: "DELETE" });
    loadStudents();
}

/* RESET */
function resetForm() {
    document.querySelectorAll("#modal input").forEach(i => i.value = "");
}

/* INIT */
window.onload = loadStudents;
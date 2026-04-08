let currentPage = 0;
const pageSize = 4;
let currentSort = "";
let editId = null;

window.onload = () => {
    if (window.location.pathname === '/dashboard') {
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = '/login';
        } else {
            loadStudents();
        }
    }
};

async function register() {
    const un = document.getElementById("username").value;
    const pw = document.getElementById("password").value;
    if (!un || !pw) return alert("Fill all fields");

    const res = await fetch('/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: un, password: pw, role: 'USER' })
    });
    if (res.ok) {
        alert("Registered! Please login.");
        window.location.href = '/login';
    } else {
        alert("Failed to register. Username might be taken.");
    }
}

async function login() {
    const un = document.getElementById("username").value;
    const pw = document.getElementById("password").value;
    if (!un || !pw) return alert("Fill all fields");

    const res = await fetch('/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: un, password: pw })
    });
    if (res.ok) {
        const token = await res.text();
        localStorage.setItem("token", token);
        window.location.href = '/dashboard';
    } else {
        alert("Invalid credentials / Login failed");
    }
}

function logout() {
    localStorage.removeItem("token");
    window.location.href = '/login';
}

function getAuthHeaders() {
    return { 'Authorization': `Bearer ${localStorage.getItem('token')}` };
}

async function loadStudents() {
    let url = `/students/filter?page=${currentPage}&size=${pageSize}`;
    if (currentSort) url += `&sort=${currentSort}`;

    const res = await fetch(url, { headers: getAuthHeaders() });
    
    if (res.status === 401 || res.status === 403) {
        return logout();
    }
    
    if (res.ok) {
        const data = await res.json();
        const tbody = document.getElementById("studentTable");
        tbody.innerHTML = "";
        data.content.forEach(s => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td><img src="${s.imageUrl || 'https://via.placeholder.com/50'}" width="50" style="border-radius:50%"></td>
                <td>${s.name}</td>
                <td>${s.rollNumber}</td>
                <td>${s.branch}</td>
                <td>${s.skills}</td>
                <td>${s.age}</td>
                <td>${s.gender}</td>
                <td>
                    <button class="gold-btn" style="padding:5px 10px; font-size:12px; margin-right:5px;" onclick="editStudent(${s.id})">✏️</button>
                    <button class="cancel-btn" style="padding:5px 10px; font-size:12px;" onclick="deleteStudent(${s.id})">🗑️</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

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

    try {
        const res = await fetch(url, { 
            method, 
            body: formData,
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });
        
        if (res.status === 401 || res.status === 403) return logout();

        const text = await res.text();
        if (res.ok) {
            alert(editId ? "Updated ✅" : "Added ✅");
            closeModal();
            loadStudents();
        } else {
            console.error("ERROR:", text);
            alert("Failed: " + text);
        }
    } catch (err) {
        console.error("EXCEPTION:", err);
        alert("Server error ❌");
    }
}

async function deleteStudent(id) {
    if (!confirm("Are you sure?")) return;
    const res = await fetch(`/students/${id}`, { method: 'DELETE', headers: getAuthHeaders() });
    if (res.status === 401 || res.status === 403) return logout();
    if (res.ok) loadStudents();
    else alert("Failed to delete");
}

async function editStudent(id) {
    const res = await fetch(`/students/${id}`, { headers: getAuthHeaders() });
    if (res.status === 401 || res.status === 403) return logout();
    if (res.ok) {
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
        editId = id;
        document.getElementById("modalTitle").innerText = "Edit Student";
        document.getElementById("modal").style.display = "flex";
    }
}

function openModal() {
    editId = null;
    document.getElementById("modalTitle").innerText = "Add Student";
    document.getElementById("name").value = "";
    document.getElementById("rollNumber").value = "";
    document.getElementById("branch").value = "";
    document.getElementById("college").value = "";
    document.getElementById("skills").value = "";
    document.getElementById("email").value = "";
    document.getElementById("phone").value = "";
    document.getElementById("age").value = "";
    document.getElementById("gender").value = "";
    document.getElementById("file").value = "";
    document.getElementById("modal").style.display = "flex";
}

function closeModal() {
    document.getElementById("modal").style.display = "none";
}

function applySort() {
    currentSort = document.getElementById("sort").value;
    currentPage = 0;
    loadStudents();
}

function nextPage() { currentPage++; loadStudents(); }
function prevPage() { if (currentPage > 0) { currentPage--; loadStudents(); } }

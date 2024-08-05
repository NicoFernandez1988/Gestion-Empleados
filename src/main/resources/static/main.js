document.addEventListener("DOMContentLoaded", function() {
    const apiUrl = "/api/employees";
    const tasksUrl = "/api/tasks/categorized";

    const employeeList = document.getElementById("employee-list");
    const employeeForm = document.getElementById("employee-form");
    const searchInput = document.getElementById("search");
    const supervisorSearchInput = document.getElementById("supervisor-search");

    const nombreInput = document.getElementById("nombre");
    const apellidoInput = document.getElementById("apellido");
    const celularInput = document.getElementById("celular");
    const generoMasculinoCheckbox = document.getElementById("genero-masculino");
    const generoFemeninoCheckbox = document.getElementById("genero-femenino");
    const tutorSiRadio = document.getElementById("tutor-si");
    const tutorNoRadio = document.getElementById("tutor-no");
    const supervisorSelect = document.getElementById("supervisor");
    const tasksContainer = document.getElementById("tasks");

    const addEmployeeButton = document.getElementById("add-employee");
    const editEmployeeButton = document.getElementById("edit-employee");
    const deleteEmployeeButton = document.getElementById("delete-employee");
    const saveButton = document.getElementById("save-employee");

    let currentEmployeeId = null;
    let allTasks = [];
    let allEmployees = [];
    let isEditing = false;

    function fetchEmployees(query = "") {
        fetch(`${apiUrl}?query=${query}`)
            .then(response => response.json())
            .then(data => {
                data.sort((a, b) => {
                    const nameA = a.name.toLowerCase();
                    const nameB = b.name.toLowerCase();
                    const lastNameA = a.lastName.toLowerCase();
                    const lastNameB = b.lastName.toLowerCase();

                    if (nameA < nameB) return -1;
                    if (nameA > nameB) return 1;
                    if (lastNameA < lastNameB) return -1;
                    if (lastNameA > lastNameB) return 1;
                    return 0;
                });

                employeeList.innerHTML = "";
                const lowerQuery = query.toLowerCase();
                const filteredData = data.filter(employee =>
                    employee.name.toLowerCase().includes(lowerQuery) ||
                    employee.lastName.toLowerCase().includes(lowerQuery)
                );
                filteredData.forEach(employee => {
                    const li = document.createElement("li");
                    li.textContent = `${employee.name} ${employee.lastName}`;
                    li.addEventListener("click", () => loadEmployee(employee.id));
                    employeeList.appendChild(li);
                });

                allEmployees = data;
                updateSupervisorSelect(data);
            })
            .catch(error => console.error('Error fetching employees:', error));
    }

    function updateSupervisorFields() {
        const isTutorYes = tutorSiRadio.checked;
        const isEditingOrHasTutor = isEditing || tutorSiRadio.checked;

        supervisorSearchInput.disabled = !isTutorYes || !isEditing;
        supervisorSelect.disabled = !isTutorYes || !isEditing;
    }

    function loadEmployee(id) {
        fetch(`${apiUrl}/${id}`)
            .then(response => response.json())
            .then(employee => {
                currentEmployeeId = employee.id;
                nombreInput.value = employee.name;
                apellidoInput.value = employee.lastName;
                celularInput.value = employee.cellPhone || "";
                generoMasculinoCheckbox.checked = employee.gender === "MASCULINO";
                generoFemeninoCheckbox.checked = employee.gender === "FEMENINO";
                tutorSiRadio.checked = employee.supervisor !== null;
                tutorNoRadio.checked = employee.supervisor === null;
                supervisorSelect.value = employee.supervisor ? employee.supervisor.id : "";
                updateTasks(employee.tasks || []);
                disableFormInputs();
                isEditing = false;
                saveButton.disabled = true;
                toggleSupervisorFields();
                updateFormState();
                updateSupervisorFields();
            })
            .catch(error => console.error('Error loading employee:', error));
    }

    function clearForm() {
        currentEmployeeId = null;
        nombreInput.value = "";
        apellidoInput.value = "";
        celularInput.value = "";
        generoMasculinoCheckbox.checked = true;
        generoFemeninoCheckbox.checked = false;
        tutorSiRadio.checked = false;
        tutorNoRadio.checked = true;
        supervisorSelect.value = "";
        supervisorSearchInput.value = "";
        updateTasks([]);
        enableFormInputs();
        isEditing = true;
        saveButton.disabled = false;
        toggleSupervisorFields();

    }

    function updateTasks(employeeTasks) {
        fetch(tasksUrl)
            .then(response => response.json())
            .then(categorizedTasks => {
                tasksContainer.innerHTML = "";
                Object.keys(categorizedTasks).forEach(category => {
                    const categoryDiv = document.createElement('div');
                    categoryDiv.classList.add('category');

                    const categoryTitle = document.createElement('h4');
                    categoryTitle.textContent = category;
                    categoryDiv.appendChild(categoryTitle);

                    const taskContainer = document.createElement('div');
                    taskContainer.classList.add('task-container');
                    categorizedTasks[category].forEach(task => {
                        const taskItem = document.createElement('div');
                        taskItem.classList.add('task-item');

                        const taskCheckbox = document.createElement('input');
                        taskCheckbox.type = 'checkbox';
                        taskCheckbox.value = task.id;
                        if (employeeTasks.some(t => t.id === task.id)) {
                            taskCheckbox.checked = true;
                        }
                        taskCheckbox.disabled = !isEditing;

                        const taskLabel = document.createElement('label');
                        taskLabel.htmlFor = `task-${task.id}`;
                        taskLabel.textContent = task.description;

                        taskItem.appendChild(taskCheckbox);
                        taskItem.appendChild(taskLabel);
                        taskContainer.appendChild(taskItem);
                    });

                    categoryDiv.appendChild(taskContainer);
                    tasksContainer.appendChild(categoryDiv);
                });
            })
            .catch(error => console.error('Error fetching tasks:', error));
    }

    function updateSupervisorSelect(employees) {
        supervisorSelect.innerHTML = '<option value="">Ninguno</option>';
        employees.forEach(employee => {
            const option = document.createElement("option");
            option.value = employee.id;
            option.textContent = `${employee.name} ${employee.lastName}`;
            supervisorSelect.appendChild(option);
        });
    }

    function fetchTasks() {
            fetch(tasksUrl)
                .then(response => response.json())
                .then(data => {
                    allTasks = data;
                    updateTasks([]);
                })
                .catch(error => console.error('Error fetching tasks:', error));
        }

    function filterSupervisors(query) {
        const filteredSupervisors = allEmployees.filter(employee =>
            employee.name.toLowerCase().includes(query.toLowerCase()) ||
            employee.lastName.toLowerCase().includes(query.toLowerCase())
        );
        updateSupervisorSelect(filteredSupervisors);
    }

    function toggleSupervisorFields() {
            const isTutor = tutorSiRadio.checked;
            supervisorSearchInput.disabled = !isTutor || !isEditing;
            supervisorSelect.disabled = !isTutor || !isEditing;
            if (!isTutor) {
                supervisorSearchInput.value = "";
            }
        }

    function updateFormState() {
        const isDisabled = !isEditing;
        employeeForm.querySelectorAll("input, select").forEach(input => {
            input.disabled = isDisabled;
        });
        tasksContainer.querySelectorAll("input[type='checkbox']").forEach(checkbox => {
            checkbox.disabled = isDisabled;
        });
        saveButton.disabled = !isEditing;
        updateSupervisorFields();
    }

    function disableFormInputs() {
            employeeForm.querySelectorAll("input, select, textarea").forEach(input => input.disabled = true);
            supervisorSearchInput.disabled = true;
            supervisorSelect.disabled = true;
        }

        function enableFormInputs() {
            employeeForm.querySelectorAll("input, select, textarea").forEach(input => input.disabled = false);
        }

    function validateForm() {
        const name = nombreInput.value.trim();
        const lastName = apellidoInput.value.trim();
        const selectedTasks = Array.from(tasksContainer.querySelectorAll("input[type='checkbox']:checked"));

        if (!name || !lastName) {
            alert("Faltan nombre y/o apellido. Por favor, completa ambos campos.");
            return false;
        }

        if (selectedTasks.length === 0) {
            alert("Debe seleccionar al menos una tarea.");
            return false;
        }

        return true;
    }


    deleteEmployeeButton.addEventListener("click", function() {
        event.preventDefault();
        if (currentEmployeeId && confirm("¿Estás seguro de que deseas eliminar este empleado?")) {
            fetch(`${apiUrl}/${currentEmployeeId}`, {
                method: "DELETE"
            })
            .then(response => {
                if (response.ok) {
                    alert("Empleado eliminado con éxito.");
                    fetchEmployees();
                    clearForm();
                } else {
                    response.json().then(errorData => {
                        throw new Error(errorData.message);
                    });
                }
            })
            .catch(error => {
                console.error('Error deleting employee:', error);
                alert(`Error: ${error.message}`);
            });
        }
    });

    saveButton.addEventListener("click", function(event) {
        event.preventDefault();

        if (!isEditing && currentEmployeeId) {
               alert("Presiona el botón editar para modificar los datos del empleado.");
               return;
        }

        if (!validateForm()) {
            return;
        }

        const selectedTasks = Array.from(tasksContainer.querySelectorAll("input[type='checkbox']:checked")).map(cb => {
            return { id: cb.value };
        });

        const gender = generoMasculinoCheckbox.checked ? "MASCULINO" : (generoFemeninoCheckbox.checked ? "FEMENINO" : "");
        const hasSupervisor = tutorSiRadio.checked;

        const employee = {
            name: nombreInput.value,
            lastName: apellidoInput.value,
            cellPhone: celularInput.value,
            gender: gender,
            supervisorId: hasSupervisor ? supervisorSelect.value : null,
            taskIds: selectedTasks.map(task => task.id)
        };

        const method = currentEmployeeId ? "PUT" : "POST";
        const url = currentEmployeeId ? `${apiUrl}/${currentEmployeeId}` : apiUrl;

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(employee)
        })
        .then(response => {
                    if (!response.ok) {
                        return response.json().then(errorData => {
                            throw new Error(errorData.message);
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    alert(currentEmployeeId ? "Empleado modificado correctamente" : "Empleado registrado correctamente");
                    clearForm();
                    fetchEmployees();
                })
                .catch(error => {
                    if (error.message.includes("duplicado")) {
                        alert("Error al procesar la solicitud.");
                    } else {
                        alert("Ya existe un empleado con el mismo nombre y apellido.");
                    }
                    console.error('Error saving employee:', error);
                });
            });

            addEmployeeButton.addEventListener("click", clearForm);

                editEmployeeButton.addEventListener("click", function(event) {
                    event.preventDefault();
                    enableFormInputs();
                    isEditing = true;
                    saveButton.disabled = false;
                    updateSupervisorFields();
                    updateFormState();
                });

    searchInput.addEventListener("input", () => fetchEmployees(searchInput.value));
        supervisorSearchInput.addEventListener("input", () => filterSupervisors(supervisorSearchInput.value));
        tutorSiRadio.addEventListener("change", updateSupervisorFields);
        tutorNoRadio.addEventListener("change", updateSupervisorFields);
        supervisorSelect.addEventListener("change", () => {
             supervisorSearchInput.value = "";
        });

    clearForm();
    fetchTasks();
    fetchEmployees();
});

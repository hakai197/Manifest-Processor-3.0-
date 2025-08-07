<template>
  <div class="assignment-list">
    <table class="table table-hover">
      <thead class="thead-light">
        <tr>
          <th>ID</th>
          <th>Employee</th>
          <th>Trailer</th>
          <th>Door</th>
          <th>Status</th>
          
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="assignment in assignments" :key="assignment.assignmentId">
          <td>{{ assignment.assignmentId }}</td>
          <td>
            <span v-if="assignment.employeeName">
              {{ assignment.employeeName }} ({{ assignment.employeeNumber }})
            </span>
            <span v-else class="text-muted">Not assigned</span>
          </td>
          <td>
            <span v-if="assignment.trailerNumber">
              {{ assignment.trailerNumber }} ({{ assignment.trailerType }})
            </span>
            <span v-else class="text-muted">No trailer</span>
          </td>
          <td>{{ assignment.doorNumber }}</td>
          <td>
            <span :class="['badge', statusClass(assignment.status)]">
              {{ assignment.status }}
            </span>
          </td>
          <td>
            <button class="btn btn-sm btn-primary mr-2" @click="$emit('edit', assignment)">
              <i class="bi bi-pencil"></i> Edit
            </button>
            <button class="btn btn-sm btn-danger" @click="$emit('delete', assignment.assignmentId)">
              <i class="bi bi-trash"></i> Delete
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  props: {
    assignments: {
      type: Array,
      required: true
    }
  },
  methods: {
    statusClass(status) {
      switch(status.toLowerCase()) {
        case 'completed': return 'badge-success';
        case 'in progress': return 'badge-warning';
        case 'assigned': return 'badge-info';
        case 'pending': return 'badge-secondary';
        default: return 'badge-primary';
      }
    }
  }
};
</script>

<style scoped>
.assignment-list {
  margin-top: 20px;
}

.table {
  width: 100%;
  margin-bottom: 1rem;
  color: #212529;
  border-collapse: collapse;
}

.table th,
.table td {
  padding: 0.75rem;
  vertical-align: top;
  border-top: 1px solid #dee2e6;
}

.table thead th {
  vertical-align: bottom;
  border-bottom: 2px solid #dee2e6;
}

.table-hover tbody tr:hover {
  color: #212529;
  background-color: rgba(0, 0, 0, 0.075);
}

.thead-light th {
  color: #495057;
  background-color: #e9ecef;
  border-color: #dee2e6;
}

.badge {
  display: inline-block;
  padding: 0.25em 0.4em;
  font-size: 75%;
  font-weight: 700;
  line-height: 1;
  text-align: center;
  white-space: nowrap;
  vertical-align: baseline;
  border-radius: 0.25rem;
}

.badge-success {
  color: #fff;
  background-color: #28a745;
}

.badge-warning {
  color: #212529;
  background-color: #ffc107;
}

.badge-info {
  color: #fff;
  background-color: #17a2b8;
}

.badge-secondary {
  color: #fff;
  background-color: #6c757d;
}

.badge-primary {
  color: #fff;
  background-color: #007bff;
}

.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
  line-height: 1.5;
  border-radius: 0.2rem;
}

.mr-2 {
  margin-right: 0.5rem;
}

.text-muted {
  color: #6c757d !important;
}

</style>
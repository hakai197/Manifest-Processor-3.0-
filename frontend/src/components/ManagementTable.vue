<template>
  <div class="table-container">
    <table class="management-table">
      <thead>
        <tr>
          <th v-for="header in headers" :key="header">{{ header }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, index) in items" :key="index">
          <td v-for="(value, key) in item" :key="key">{{ value }}</td>
          <td v-if="$slots.actions" class="actions-cell">
            <slot name="actions" :item="item"></slot>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="items.length === 0" class="no-data">
      No data available
    </div>
  </div>
</template>

<script setup>
defineProps({
  headers: {
    type: Array,
    required: true
  },
  items: {
    type: Array,
    required: true
  }
})
</script>

<style scoped>
.table-container {
  width: 100%;
  overflow-x: auto;
}

.management-table {
  width: 100%;
  border-collapse: collapse;
}

.management-table th,
.management-table td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.management-table th {
  background-color: var(--secondary-color);
  color: white;
  position: sticky;
  top: 0;
}

.management-table tr:nth-child(even) {
  background-color: #f2f2f2;
}

.management-table tr:hover {
  background-color: #e6e6e6;
}

.actions-cell {
  white-space: nowrap;
}

.no-data {
  padding: 1rem;
  text-align: center;
  color: #666;
  font-style: italic;
}

.action-btn {
  padding: 0.25rem 0.5rem;
  margin: 0 0.25rem;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}

.edit-btn {
  background-color: var(--secondary-color);
  color: white;
}

.delete-btn {
  background-color: var(--accent-color);
  color: white;
}

@media (max-width: 768px) {
  .management-table {
    display: block;
    overflow-x: auto;
  }
}
</style>
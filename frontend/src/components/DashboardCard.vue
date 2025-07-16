<template>
  <div class="dashboard-card" :class="{ 'wide-card': wide }">
    <div class="card-header">
      {{ title }}
      <div v-if="$slots.actions" class="card-actions">
        <slot name="actions"></slot>
      </div>
    </div>
    <div class="card-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  wide: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped>
.dashboard-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: 400px;
  min-height: 300px;
}

.wide-card {
  grid-column: span 3;
}

.card-header {
  background-color: var(--secondary-color);
  color: white;
  padding: 1rem;
  font-weight: bold;
  font-size: 1.2rem;
  text-align: center;
  position: relative;
}

.card-content {
  padding: 0.5rem;
  flex-grow: 1;
  overflow-y: auto;
}

.card-actions {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
}

@media (max-width: 768px) {
  .wide-card {
    grid-column: span 1;
  }
}
</style>
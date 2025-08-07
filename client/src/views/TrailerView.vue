<template>
  <div class="container">
    <router-link to="/" class="btn btn-secondary mb-4">Home</router-link>
    <h1 class="mb-4">Trailer Management</h1>
    
    
    <div class="row mb-4">
      <div class="col-md-8">
        <div class="input-group">
          <input
            type="text"
            class="form-control"
            placeholder="Search trailers by number..."
            v-model="searchQuery"
            @keyup.enter="handleSearch"
          >
          <div class="input-group-append">
            <button 
              class="btn btn-primary" 
              type="button" 
              @click="handleSearch"
              :disabled="loading"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm"></span>
              Search
            </button>
            <button 
              class="btn btn-outline-secondary" 
              type="button" 
              @click="clearSearch"
              :disabled="loading || !searchQuery"
            >
              Clear
            </button>
          </div>
        </div>
      </div>
      <div class="col-md-4 text-right">
        <button 
          class="btn btn-success" 
          @click="handleAddClick"
          :disabled="!isAdmin"
          :title="isAdmin ? '' : 'Only administrators can add trailers'"
        >
          <i class="bi bi-plus-lg"></i> Add Trailer
        </button>
      </div>
    </div>
    
    
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="sr-only">Loading...</span>
      </div>
    </div>
    
    <div v-if="error" class="alert alert-danger">{{ error }}</div>
    
    <div v-if="searchMode && trailers.length === 0" class="alert alert-info">
      No trailers found matching "{{ searchQuery }}"
    </div>
    
    
    <TrailerList 
      ref="trailerList"
      :trailers="trailers" 
      @edit="handleEdit" 
      @delete="handleDelete" 
      class="mb-4"
    />
    
    
    <div ref="formContainer">
      <TrailerForm 
        v-if="showForm" 
        :trailer="currentTrailer" 
        @save="handleSave" 
        @cancel="handleCancel" 
      />
    </div>
    <ConfirmModal
    :show="showDeleteModal"
    title="Confirm Delete"
    message="Are you sure you want to delete this trailer?"
    @confirm="confirmDelete"
    @cancel="cancelDelete"
  />
  </div>
</template>

<script>
import TrailerService from '../services/TrailerService';
import TrailerList from '../components/TrailerList.vue';
import TrailerForm from '../components/TrailerForm.vue';
import ConfirmModal from '../components/ConfirmModal.vue';

export default {
  name: 'TrailersView',
  components: {
    TrailerList,
    TrailerForm,
    ConfirmModal
  },
  data() {
    return {
      trailers: [],
      showForm: false,
      currentTrailer: null,
      error: null,
      loading: false,
      searchQuery: '',
      searchMode: false,
      showDeleteModal: false,
      trailerToDelete: null
    };
  },
  computed: {
    isAdmin() {
      return this.$store.state.user?.role === 'ROLE_ADMIN';
    }
  },
  created() {
    this.fetchTrailers();
  },
  methods: {
    fetchTrailers() {
      this.loading = true;
      this.error = null;
      this.searchMode = false;
      
      TrailerService.getAll()
        .then(response => {
          this.trailers = response.data;
        })
        .catch(error => {
          this.handleApiError(error, 'fetching trailers');
        })
        .finally(() => {
          this.loading = false;
        });
    },
    handleSearch() {
      if (!this.searchQuery.trim()) {
        return;
      }
      
      this.loading = true;
      this.error = null;
      this.searchMode = true;
      
      TrailerService.getByNumber(this.searchQuery.trim())
        .then(response => {
          this.trailers = [response.data];
        })
        .catch(error => {
          if (error.response && error.response.status === 404) {
            this.trailers = [];
          } else {
            this.handleApiError(error, 'searching trailers');
          }
        })
        .finally(() => {
          this.loading = false;
        });
    },
    clearSearch() {
      this.searchQuery = '';
      if (this.searchMode) {
        this.fetchTrailers();
      }
    },
    handleApiError(error, action) {
      console.error(`Error ${action}:`, error);
      
      if (error.response && error.response.status === 401) {
        this.$store.dispatch('logout');
        this.$router.push('/login');
        this.$store.dispatch('showError', 'Session expired. Please log in again.');
      } else if (error.response && error.response.status === 403) {
        this.error = 'You do not have permission to perform this action';
        this.$store.dispatch('showError', this.error);
      } else {
        this.error = `Failed while ${action}. Please try again.`;
        this.$store.dispatch('showError', this.error);
      }
    },
    handleAddClick() {
      if (this.isAdmin) {
        this.currentTrailer = {
          trailerId: null,
          trailerNumber: '',
          trailerType: '',
          shipperId: 0,
          status: 'Unassigned'
        };
        this.showForm = true;
        this.$nextTick(() => {
          this.scrollToForm();
        });
      } else {
        this.$store.dispatch('showError', 'Only administrators can add new trailers');
      }
    },
    handleEdit(trailer) {
      this.currentTrailer = { ...trailer };
      this.showForm = true;
      this.$nextTick(() => {
        this.scrollToForm();
      });
    },
    handleSave(trailer) {
      const savePromise = trailer.trailerId 
        ? TrailerService.update(trailer.trailerNumber, trailer)
        : TrailerService.create(trailer);

      savePromise
        .then(() => {
          this.$store.dispatch('showSuccess', 
            trailer.trailerId 
              ? 'Trailer updated successfully' 
              : 'Trailer created successfully'
          );
          this.showForm = false;
          this.currentTrailer = null;
          this.searchMode ? this.handleSearch() : this.fetchTrailers();
        })
        .catch(error => {
          this.handleApiError(error, 'saving trailer');
        });
    },
     handleDelete(trailerNumber) {
      if (this.isAdmin) {
        this.trailerToDelete = trailerNumber;
        this.showDeleteModal = true;
      } else {
        this.$store.dispatch('showError', 'Only administrators can delete trailers');
      }
    },
    confirmDelete() {
      this.loading = true;
      TrailerService.delete(this.trailerToDelete)
        .then(() => {
          this.$store.dispatch('showSuccess', 'Trailer deleted successfully');
          this.searchMode ? this.handleSearch() : this.fetchTrailers();
        })
        .catch(error => {
          this.handleApiError(error, 'deleting trailer');
        })
        .finally(() => {
          this.loading = false;
          this.showDeleteModal = false;
          this.trailerToDelete = null;
        });
    },
    cancelDelete() {
      this.showDeleteModal = false;
      this.trailerToDelete = null;
    },
    handleCancel() {
      this.showForm = false;
      this.currentTrailer = null;
      this.scrollToList();
    },
    scrollToForm() {
      this.$refs.formContainer?.scrollIntoView({ 
        behavior: 'smooth',
        block: 'nearest'
      });
    },
    scrollToList() {
      this.$refs.trailerList?.$el?.scrollIntoView({ 
        behavior: 'smooth',
        block: 'start'
      });
    }
  }
};
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.btn {
  margin-right: 8px;
}

.alert {
  margin-top: 20px;
  margin-bottom: 20px;
}

.spinner-border {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  vertical-align: text-bottom;
  border: 0.25em solid currentColor;
  border-right-color: transparent;
  border-radius: 50%;
  animation: spinner-border 0.75s linear infinite;
}

.input-group {
  margin-bottom: 0;
}

@keyframes spinner-border {
  to { transform: rotate(360deg); }
}

@media (max-width: 425px) {
  .col-md-4.text-right, .col-md-8 {
    margin-top: 10px;
  }
}
</style>
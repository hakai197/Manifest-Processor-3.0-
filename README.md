# Manifest Processor 3.0 - Frontend Architecture Guide (Updated)

## Overview
Manifest Processor 3.0 is a comprehensive logistics management system built with Vue.js. The application now features expanded functionality for Shipper, Order, and Unloader management alongside the existing Trailer and Assignment modules.

## Application Flow

### 1. Homepage (Navigation Hub)
- Central dashboard with quick access to all modules
- Status overview widgets for key operations
- Unified navigation pattern across all modules

### 2. Entity Management Modules

#### Trailer Management (Existing)
- **TrailerList**: Displays all trailers with sorting/filtering
- **TrailerForm**: Create/edit with validation

#### Shipper Management
- **ShipperList**:
  - Paginated table view
  - Search by name/location
  - Status filtering (active/inactive)
  
- **ShipperForm**:
  - Contact information validation
  - Rate card association
  - Service area mapping

#### Order Management
- **OrderList**:
  - Timeline view of orders
  - Filter by status (pending, active, completed)
  - Bulk operations
  
- **OrderForm**:
  - Multi-step creation process
  - Shipper-auto-complete
  - Dynamic field rendering based on order type
  - Document attachment handling

#### Unloader Management
- **UnloaderList**:
  - Skills/qualifications filtering
  - Availability calendar integration
  - Capacity utilization indicators

- **UnloaderForm**:
  - Certification tracking
  - Equipment proficiency selection
  - Availability schedule management

### 3. Assignment Module (Enhanced)
Now integrates with all entity types:

#### AssignmentForm Features:
1. **Expanded Relationship Mapping**:
   - Trailer ↔ Order compatibility checking
   - Shipper-specific unloader requirements
   - Equipment needs matching

2. **Enhanced Dynamic UI**:
   - Cascading dropdowns (Shipper → Order → Trailer)
   - Unloader availability indicators
   - Conflict detection

3. **Advanced Validation**:
   - Cross-entity validation rules
   - Temporal conflict checking
   - Capacity validation

## Architectural Patterns

### Unified Component Structure
All entity modules follow the same pattern:
```
EntityList.vue       // List view with controls
EntityForm.vue       // Form with validation
EntityCard.vue       // Compact display component
EntityService.js     // API communication
```

### Shared Features Across Modules
1. **Consistent CRUD Operations**:
   - Create/Read/Update/Delete patterns
   - Common API error handling
   - Uniform success/error notifications

2. **Standardized Form Components**:
   - Address input component
   - Contact information section
   - Status toggle control

3. **Common List Features**:
   - Pagination controls
   - Column sorting
   - Export functionality

### Enhanced State Management
- Vuex modules for each entity type
- Shared mutations for common operations
- Normalized state structure for relationships

## Development Practices

### Code Reuse Strategy
- Shared form components (BaseInput, BaseSelect)
- Common list mixins (Sorting, Pagination)
- Unified API service architecture

### Testing Approach
- Standardized test suites for all entity modules
- Shared test utilities for form validation
- Cross-module integration tests

## Getting Started

### Module-Specific Scripts
```bash
# Generate new entity module scaffolding
npm run generate:module -- Shipper

# Run module-specific tests
npm run test:module -- Unloader
```

## Future Enhancements
1. **Cross-Module Relationships View**
   - Visualize connections between entities
   - Dependency mapping

2. **Bulk Operations**
   - Multi-entity updates
   - Template-based imports

3. **Advanced Reporting**
   - Module-specific analytics
   - Combined performance dashboards

This expanded architecture maintains consistency across all management modules while allowing for module-specific functionality where needed. The system now provides complete end-to-end logistics management capabilities with unified patterns for maintainability and developer experience.

# Suggested Additional Tests

This document outlines recommended additional tests to further improve code coverage and reliability of the Citrus Admin Android application.

---

## 1. Additional ViewModel Tests

### AnalyticsViewModel Tests (Priority: HIGH)
**Location**: `app/src/test/kotlin/com/citrus/admin/ui/analytics/AnalyticsViewModelTest.kt`

Suggested tests:
```kotlin
- ✨ `initial state loads analytics for default period`
- ✨ `switching period updates analytics data`
- ✨ `refresh analytics calls repository`
- ✨ `analytics displays correct metrics for TODAY period`
- ✨ `analytics displays correct metrics for WEEK period`
- ✨ `analytics displays correct metrics for MONTH period`
- ✨ `analytics displays correct metrics for YEAR period`
- ✨ `error handling when fetching analytics fails`
- ✨ `loading state toggles correctly during refresh`
- ✨ `clearError resets error state`
```

**Estimated Test Count**: 10 tests
**Complexity**: Medium

---

### DashboardViewModel Tests (Priority: HIGH)
**Location**: `app/src/test/kotlin/com/citrus/admin/ui/dashboard/DashboardViewModelTest.kt`

Suggested tests:
```kotlin
- ✨ `dashboard aggregates data from multiple repositories`
- ✨ `dashboard shows pending receipts count`
- ✨ `dashboard shows active users count`
- ✨ `dashboard shows unread alerts count`
- ✨ `refresh updates all dashboard data`
- ✨ `navigation to different screens works`
- ✨ `error in one data source doesn't break entire dashboard`
- ✨ `loading states for each section are independent`
- ✨ `dashboard handles empty state correctly`
- ✨ `dashboard handles error state correctly`
```

**Estimated Test Count**: 10 tests
**Complexity**: High (multiple repositories)

---

## 2. DAO Tests (Priority: MEDIUM)

### UserDao Tests
**Location**: `app/src/test/kotlin/com/citrus/admin/data/local/dao/UserDaoTest.kt`

These tests should use Room's in-memory database:
```kotlin
- ✨ `insertUser saves user to database`
- ✨ `getUserById retrieves correct user`
- ✨ `getAllUsers returns all users`
- ✨ `getActiveUsers filters by active status`
- ✨ `updateUser modifies existing user`
- ✨ `deleteUser removes user from database`
- ✨ `getUserCount returns accurate count`
- ✨ `getActiveUserCount returns accurate count`
- ✨ `searchUsers finds users by name or email`
- ✨ `getUsersByStatus filters correctly`
- ✨ `inserting duplicate user replaces existing`
- ✨ `flow updates when data changes`
```

**Estimated Test Count**: 12 tests
**Complexity**: Medium (requires Room in-memory DB setup)

---

### ReceiptDao Tests
**Location**: `app/src/test/kotlin/com/citrus/admin/data/local/dao/ReceiptDaoTest.kt`

```kotlin
- ✨ `insertReceipt saves receipt to database`
- ✨ `getReceiptById retrieves correct receipt`
- ✨ `getAllReceipts returns all receipts sorted by submittedAt`
- ✨ `getReceiptsByStatus filters correctly`
- ✨ `getReceiptsByUser returns user's receipts`
- ✨ `updateReceipt modifies existing receipt`
- ✨ `deleteReceipt removes from database`
- ✨ `getReceiptCountByStatus returns accurate count`
- ✨ `getTotalApprovedAmount calculates sum correctly`
- ✨ `getTotalApprovedAmount returns null for no approved receipts`
- ✨ `flow updates when receipts change`
```

**Estimated Test Count**: 11 tests
**Complexity**: Medium

---

### AlertDao Tests
**Location**: `app/src/test/kotlin/com/citrus/admin/data/local/dao/AlertDaoTest.kt`

```kotlin
- ✨ `insertAlerts saves multiple alerts`
- ✨ `getAllActiveAlerts returns non-dismissed alerts`
- ✨ `getUnreadAlerts filters by read status`
- ✨ `getUnreadAlertCount returns accurate count`
- ✨ `markAsRead updates alert status`
- ✨ `dismissAlert marks alert as dismissed`
- ✨ `alerts sorted by timestamp descending`
- ✨ `flow updates when alerts change`
```

**Estimated Test Count**: 8 tests
**Complexity**: Medium

---

## 3. Integration Tests (Priority: HIGH)

### Repository Integration Tests
**Location**: `app/src/androidTest/kotlin/com/citrus/admin/data/repository/`

These tests verify the full stack (DAO + Repository):
```kotlin
- ✨ `UserRepository end-to-end operations`
- ✨ `ReceiptRepository end-to-end operations`
- ✨ `AlertRepository end-to-end operations`
- ✨ `AnalyticsRepository aggregates real data from multiple sources`
- ✨ `concurrent operations handle correctly`
- ✨ `transaction rollback on errors`
```

**Estimated Test Count**: 15-20 tests
**Complexity**: High

---

## 4. API Service Tests (Priority: MEDIUM)

### ApiService Mock Tests
**Location**: `app/src/test/kotlin/com/citrus/admin/data/remote/ApiServiceTest.kt`

Using MockWebServer:
```kotlin
- ✨ `getUsers returns parsed user list`
- ✨ `getReceipts returns parsed receipt list`
- ✨ `getAlerts returns parsed alert list`
- ✨ `getAnalytics returns parsed analytics`
- ✨ `updateUserStatus sends correct request`
- ✨ `reviewReceipt sends correct data`
- ✨ `dismissAlert sends correct request`
- ✨ `API handles 401 unauthorized`
- ✨ `API handles 404 not found`
- ✨ `API handles 500 server error`
- ✨ `API handles network timeout`
- ✨ `API retry logic works correctly`
- ✨ `authentication headers are included`
```

**Estimated Test Count**: 13 tests
**Complexity**: Medium

---

## 5. DTO Conversion Tests (Priority: MEDIUM)

### DTO to Entity Mappings
**Location**: `app/src/test/kotlin/com/citrus/admin/data/remote/dto/`

```kotlin
// UserDto Tests
- ✨ `UserDto to UserEntity conversion preserves data`
- ✨ `UserEntity to UserDto conversion preserves data`
- ✨ `round trip conversion works correctly`

// ReceiptDto Tests
- ✨ `ReceiptDto to ReceiptEntity conversion preserves data`
- ✨ `ReceiptEntity to ReceiptDto conversion preserves data`
- ✨ `round trip conversion works correctly`

// AlertDto Tests
- ✨ `AlertDto to AlertEntity conversion preserves data`
- ✨ `AlertEntity to AlertDto conversion preserves data`
- ✨ `round trip conversion works correctly`

// AnalyticsDto Tests
- ✨ `AnalyticsDto parsing works correctly`
- ✨ `handles missing optional fields`
- ✨ `handles null values gracefully`
```

**Estimated Test Count**: 12 tests
**Complexity**: Low

---

## 6. Edge Case & Boundary Tests (Priority: HIGH)

### User Management Edge Cases
```kotlin
- ✨ `updating non-existent user handles gracefully`
- ✨ `deleting already deleted user handles gracefully`
- ✨ `very long user names are handled`
- ✨ `special characters in email addresses`
- ✨ `unicode characters in names`
- ✨ `bulk operations with 1000+ users`
- ✨ `empty string search query`
- ✨ `search with special regex characters`
```

### Receipt Management Edge Cases
```kotlin
- ✨ `receipts with zero amount`
- ✨ `receipts with very large amounts (overflow test)`
- ✨ `receipts with negative amounts (validation)`
- ✨ `receipts with decimal precision edge cases`
- ✨ `approving already approved receipt`
- ✨ `rejecting already rejected receipt`
- ✨ `concurrent approval/rejection of same receipt`
- ✨ `very long receipt descriptions`
```

### Alert Management Edge Cases
```kotlin
- ✨ `marking already read alert as read`
- ✨ `dismissing already dismissed alert`
- ✨ `concurrent mark/dismiss operations`
- ✨ `alerts with timestamps in the future`
- ✨ `alerts with timestamps way in the past`
- ✨ `very long alert messages`
```

**Estimated Test Count**: 20+ tests
**Complexity**: Medium

---

## 7. Performance & Load Tests (Priority: LOW)

### Performance Tests
**Location**: `app/src/test/kotlin/com/citrus/admin/performance/`

```kotlin
- ✨ `loading 1000+ users completes within acceptable time`
- ✨ `loading 1000+ receipts completes within acceptable time`
- ✨ `search with large dataset performs well`
- ✨ `database query optimization verification`
- ✨ `memory usage stays within limits`
- ✨ `flow emission rate is reasonable`
```

**Estimated Test Count**: 6 tests
**Complexity**: High

---

## 8. UI/Compose Tests (Priority: MEDIUM)

### Composable Screen Tests
**Location**: `app/src/androidTest/kotlin/com/citrus/admin/ui/`

```kotlin
// UserManagementScreen Tests
- ✨ `user list displays correctly`
- ✨ `search filter works`
- ✨ `user status filter works`
- ✨ `clicking user opens detail`
- ✨ `delete confirmation dialog shows`
- ✨ `refresh updates user list`

// ReceiptReviewScreen Tests
- ✨ `receipt list displays correctly`
- ✨ `approve button calls approval`
- ✨ `reject button shows reject dialog`
- ✨ `filter changes update list`

// AlertsScreen Tests
- ✨ `alerts display correctly`
- ✨ `clicking alert marks as read`
- ✨ `dismiss button dismisses alert`
- ✨ `unread count badge updates`

// AnalyticsScreen Tests
- ✨ `metrics display correctly`
- ✨ `period selector changes data`
- ✨ `charts render properly`
- ✨ `refresh updates analytics`

// DashboardScreen Tests
- ✨ `dashboard widgets display`
- ✨ `navigation to screens works`
- ✨ `quick actions work correctly`
```

**Estimated Test Count**: 25-30 tests
**Complexity**: High

---

## 9. Navigation Tests (Priority: MEDIUM)

### Navigation Flow Tests
**Location**: `app/src/androidTest/kotlin/com/citrus/admin/navigation/`

```kotlin
- ✨ `navigation from dashboard to user management`
- ✨ `navigation from dashboard to receipt review`
- ✨ `navigation from dashboard to analytics`
- ✨ `navigation from dashboard to alerts`
- ✨ `back navigation works correctly`
- ✨ `deep linking to specific screens`
- ✨ `navigation state is preserved`
- ✨ `navigation with arguments works`
```

**Estimated Test Count**: 8 tests
**Complexity**: Medium

---

## 10. Service Tests (Priority: LOW)

### CitrusMessagingService Tests
**Location**: `app/src/test/kotlin/com/citrus/admin/services/CitrusMessagingServiceTest.kt`

```kotlin
- ✨ `onMessageReceived processes notification`
- ✨ `onMessageReceived creates system notification`
- ✨ `notification clicking opens correct screen`
- ✨ `notification data is parsed correctly`
- ✨ `notification with missing data handles gracefully`
- ✨ `notification updates alert repository`
```

**Estimated Test Count**: 6 tests
**Complexity**: Medium

---

## 11. Dependency Injection Tests (Priority: LOW)

### Hilt Module Tests
**Location**: `app/src/test/kotlin/com/citrus/admin/di/`

```kotlin
- ✨ `DatabaseModule provides database singleton`
- ✨ `DatabaseModule provides correct DAO instances`
- ✨ `AppModule provides API service`
- ✨ `AppModule provides Retrofit with correct config`
- ✨ `RepositoryModule binds all repositories`
- ✨ `circular dependency check`
```

**Estimated Test Count**: 6 tests
**Complexity**: Low

---

## 12. Error Handling & Recovery Tests (Priority: HIGH)

### Error Scenarios
```kotlin
- ✨ `database corruption recovery`
- ✨ `API timeout retry logic`
- ✨ `network unavailable graceful degradation`
- ✨ `partial data sync recovery`
- ✨ `corrupted cache handling`
- ✨ `out of memory handling`
- ✨ `concurrent modification conflicts`
```

**Estimated Test Count**: 7+ tests
**Complexity**: High

---

## Test Priority Summary

| Priority | Category | Estimated Tests | Impact |
|----------|----------|-----------------|--------|
| **HIGH** | ViewModels (remaining) | 20 | Critical |
| **HIGH** | Integration Tests | 20 | Critical |
| **HIGH** | Edge Cases | 20 | Critical |
| **HIGH** | Error Handling | 7 | Critical |
| **MEDIUM** | DAO Tests | 31 | Important |
| **MEDIUM** | API Service Tests | 13 | Important |
| **MEDIUM** | DTO Tests | 12 | Important |
| **MEDIUM** | UI Tests | 30 | Important |
| **MEDIUM** | Navigation Tests | 8 | Important |
| **LOW** | Performance Tests | 6 | Nice to have |
| **LOW** | Service Tests | 6 | Nice to have |
| **LOW** | DI Tests | 6 | Nice to have |

**Total Suggested Tests**: ~179 additional tests

---

## Implementation Roadmap

### Phase 1 (Week 1-2): High Priority
1. Complete remaining ViewModel tests (AnalyticsViewModel, DashboardViewModel)
2. Implement edge case tests
3. Add error handling tests
4. Begin integration tests

### Phase 2 (Week 3-4): Medium Priority
1. Implement DAO tests with in-memory database
2. Add API service tests with MockWebServer
3. Create DTO conversion tests
4. Begin UI/Compose tests

### Phase 3 (Week 5-6): Low Priority & Polish
1. Add navigation tests
2. Implement performance tests
3. Add service tests
4. Implement DI tests
5. Increase overall coverage to 95%+

---

## Benefits of Implementing These Tests

1. **Increased Confidence**: Higher test coverage reduces fear of breaking changes
2. **Better Documentation**: Tests serve as living documentation of expected behavior
3. **Faster Development**: Catching bugs early saves debugging time
4. **Easier Refactoring**: Comprehensive tests enable safe refactoring
5. **Production Stability**: Fewer bugs reach production
6. **Team Collaboration**: Tests clarify component contracts
7. **Regression Prevention**: Tests prevent old bugs from resurfacing

---

## Getting Started

To implement these tests:

1. **Set up test infrastructure** (Room in-memory DB, MockWebServer)
2. **Create test base classes** for common setup
3. **Implement tests incrementally** following the priority order
4. **Run tests frequently** during development
5. **Monitor coverage** and aim for 90%+ in critical paths
6. **Review and refactor** tests alongside production code

---

## Resources

- [Android Testing Guide](https://developer.android.com/training/testing)
- [Testing Coroutines](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/)
- [Room Testing](https://developer.android.com/training/data-storage/room/testing-db)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [MockK Documentation](https://mockk.io/)

---

**Document Version**: 1.0
**Last Updated**: 2025-11-19
**Estimated Implementation Time**: 6 weeks (with 2 developers)

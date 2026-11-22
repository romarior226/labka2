package org.example.dto;

public record ReliabilityResponse(Long readerId,
                                  boolean reliable,
                                  int returnedLast30Days,
                                  boolean allReturnedOnTime,
                                  boolean hasOverdueLoans,
                                  int activeLoans,
                                  int maxActiveLoans) {
}

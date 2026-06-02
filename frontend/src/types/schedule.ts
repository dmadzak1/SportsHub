export interface ScheduleDto {
  scheduleId: number;
  facilityId: number;
  date: string;
  timeSlot: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

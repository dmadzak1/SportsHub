export interface FacilityDto {
  facilityId: number;
  name: string;
  type: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

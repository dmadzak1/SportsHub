export interface PackageDto {
  packageId: number;
  name: string;
  price: number;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

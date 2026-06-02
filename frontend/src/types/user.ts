export interface UserDto {
  userId: number;
  email: string;
  password: string;
  roleId: number | null;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

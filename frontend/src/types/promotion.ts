export interface PromotionDto {
  promotionId: number;
  packageId: number;
  discount: number;
  validUntil: string | null;
}

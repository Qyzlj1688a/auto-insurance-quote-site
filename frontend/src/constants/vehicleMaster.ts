export interface VehicleMasterItem {
  modelCode: string;
  maker: string;
  carName: string;
  vehicleType: string;
}

export const VEHICLE_MASTER: VehicleMasterItem[] = [
  // 軽自動車 (KEI)
  { modelCode: "JF3", maker: "ホンダ", carName: "N-BOX", vehicleType: "KEI" },
  { modelCode: "JF4", maker: "ホンダ", carName: "N-BOX", vehicleType: "KEI" },
  { modelCode: "LA650S", maker: "ダイハツ", carName: "タント", vehicleType: "KEI" },
  { modelCode: "MK53S", maker: "スズキ", carName: "スペーシア", vehicleType: "KEI" },
  { modelCode: "MH55S", maker: "スズキ", carName: "ワゴンR", vehicleType: "KEI" },

  // コンパクトカー (COMPACT)
  { modelCode: "E13", maker: "日産", carName: "ノート", vehicleType: "COMPACT" },
  { modelCode: "KSP210", maker: "トヨタ", carName: "ヤリス", vehicleType: "COMPACT" },
  { modelCode: "GR1", maker: "ホンダ", carName: "フィット", vehicleType: "COMPACT" },
  { modelCode: "DJLFS", maker: "マツダ", carName: "MAZDA2", vehicleType: "COMPACT" },

  // セダン (SEDAN)
  { modelCode: "ZVW50", maker: "トヨタ", carName: "プリウス", vehicleType: "SEDAN" },
  { modelCode: "ZVW30", maker: "トヨタ", carName: "プリウス", vehicleType: "SEDAN" },
  { modelCode: "MXWH60", maker: "トヨタ", carName: "プリウス", vehicleType: "SEDAN" },
  { modelCode: "FC1", maker: "ホンダ", carName: "シビック", vehicleType: "SEDAN" },

  // ミニバン (MINIVAN)
  { modelCode: "RP6", maker: "ホンダ", carName: "ステップワゴン", vehicleType: "MINIVAN" },
  { modelCode: "RP3", maker: "ホンダ", carName: "ステップワゴン", vehicleType: "MINIVAN" },
  { modelCode: "MZRA90", maker: "トヨタ", carName: "ヴォクシー", vehicleType: "MINIVAN" },
  { modelCode: "C28", maker: "日産", carName: "セレナ", vehicleType: "MINIVAN" },

  // SUV (SUV)
  { modelCode: "MXUA80", maker: "トヨタ", carName: "ハリアー", vehicleType: "SUV" },
  { modelCode: "RV5", maker: "ホンダ", carName: "ヴェゼル", vehicleType: "SUV" },
  { modelCode: "KF5P", maker: "マツダ", carName: "CX-5", vehicleType: "SUV" },
  { modelCode: "SK9", maker: "スバル", carName: "フォレスター", vehicleType: "SUV" },
  { modelCode: "VJA300W", maker: "トヨタ", carName: "ランドクルーザー", vehicleType: "SUV" },
];

export const findVehicleByCode = (code: string): VehicleMasterItem | undefined => {
  const normalized = code.trim().toUpperCase();
  return VEHICLE_MASTER.find((item) => item.modelCode.toUpperCase() === normalized);
};

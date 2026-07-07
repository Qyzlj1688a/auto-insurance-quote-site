import React from "react";
import { vi, describe, test, expect } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { StepUserInfo } from "../pages/wizard/StepUserInfo";
import { QuoteCreateRequest } from "../types";

describe("StepUserInfo Component (SC-002)", () => {
  const defaultData: QuoteCreateRequest = {
    driverAge: "",
    licenseColor: "",
    usageType: "",
    annualMileage: "",
    driverRange: "",
    hasCurrentInsurance: null,
    grade: "",
    accidentTerm: "",
    maker: "",
    carName: "",
    firstRegistrationYearMonth: "",
    vehicleType: "",
    vehicleInsurance: false,
    propertyDamageLimit: "UNLIMITED",
    personalInjuryAmount: "UNLIMITED",
    lawyerOption: false,
    roadService: false,
  };

  const mockUpdateData = vi.fn();
  const mockOnNext = vi.fn();
  const mockOnPrev = vi.fn();

  test("renders all form labels", () => {
    render(
      <StepUserInfo
        data={defaultData}
        updateData={mockUpdateData}
        onNext={mockOnNext}
        onPrev={mockOnPrev}
      />
    );

    expect(screen.getByText("運転者年齢")).toBeInTheDocument();
    expect(screen.getAllByText("免許証の色")[0]).toBeInTheDocument();
    expect(screen.getByText("使用目的")).toBeInTheDocument();
    expect(screen.getByText("年間走行距離 (km)")).toBeInTheDocument();
    expect(screen.getAllByText("運転者範囲")[0]).toBeInTheDocument();
  });

  test("validates driverAge bounds on blur", () => {
    const dataWithInvalidAge = { ...defaultData, driverAge: 17 };
    render(
      <StepUserInfo
        data={dataWithInvalidAge}
        updateData={mockUpdateData}
        onNext={mockOnNext}
        onPrev={mockOnPrev}
      />
    );

    const ageInput = screen.getByPlaceholderText("例：35");
    fireEvent.blur(ageInput);

    expect(screen.getByText("18歳以上100歳以下で入力してください")).toBeInTheDocument();
  });

  test("validates annualMileage bounds on blur", () => {
    const dataWithInvalidMileage = { ...defaultData, annualMileage: 35000 };
    render(
      <StepUserInfo
        data={dataWithInvalidMileage}
        updateData={mockUpdateData}
        onNext={mockOnNext}
        onPrev={mockOnPrev}
      />
    );

    const mileageInput = screen.getByPlaceholderText("例：8000");
    fireEvent.blur(mileageInput);

    expect(screen.getByText("0以上30000以下で入力してください")).toBeInTheDocument();
  });

  test("enables Next button when form is completely valid and triggers onNext", () => {
    const validData: QuoteCreateRequest = {
      ...defaultData,
      driverAge: 35,
      licenseColor: "GOLD",
      usageType: "PRIVATE",
      annualMileage: 8000,
      driverRange: "SELF",
    };

    render(
      <StepUserInfo
        data={validData}
        updateData={mockUpdateData}
        onNext={mockOnNext}
        onPrev={mockOnPrev}
      />
    );

    const nextButton = screen.getByRole("button", { name: "次へ" });
    expect(nextButton).not.toBeDisabled();

    fireEvent.click(nextButton);
    expect(mockOnNext).toHaveBeenCalled();
  });
});

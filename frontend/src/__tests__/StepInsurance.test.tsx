import React from "react";
import { vi, describe, test, expect } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { StepInsurance } from "../pages/wizard/StepInsurance";
import { QuoteCreateRequest } from "../types";

describe("StepInsurance Component (SC-003)", () => {
  const defaultData: QuoteCreateRequest = {
    driverAge: 35,
    licenseColor: "GOLD",
    usageType: "PRIVATE",
    annualMileage: 8000,
    driverRange: "SELF",
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

  test("renders choice options successfully", () => {
    render(
      <StepInsurance
        data={defaultData}
        updateData={mockUpdateData}
        onNext={mockOnNext}
        onPrev={mockOnPrev}
      />
    );

    expect(screen.getByText("現在、他社の自動車保険に加入していますか")).toBeInTheDocument();
    expect(screen.getByText("加入している")).toBeInTheDocument();
    expect(screen.getByText("加入していない")).toBeInTheDocument();
  });

  test("shows additional inputs when '加入している' is selected", () => {
    const dataInsured = {
      ...defaultData,
      hasCurrentInsurance: true,
    };

    render(
      <StepInsurance
        data={dataInsured}
        updateData={mockUpdateData}
        onNext={mockOnNext}
        onPrev={mockOnPrev}
      />
    );

    expect(screen.getByText("等級 (1〜20)")).toBeInTheDocument();
    expect(screen.getByText("事故有係数適用期間 (0〜6年)")).toBeInTheDocument();
  });

  test("validates grade bounds on blur", () => {
    const dataInvalidGrade = {
      ...defaultData,
      hasCurrentInsurance: true,
      grade: 25,
    };

    render(
      <StepInsurance
        data={dataInvalidGrade}
        updateData={mockUpdateData}
        onNext={mockOnNext}
        onPrev={mockOnPrev}
      />
    );

    const gradeInput = screen.getByPlaceholderText("例：20");
    fireEvent.blur(gradeInput);

    expect(screen.getByText("1〜20の等級を入力してください")).toBeInTheDocument();
  });

  test("enables Next button when '加入していない' is selected", () => {
    const dataNotInsured = {
      ...defaultData,
      hasCurrentInsurance: false,
    };

    render(
      <StepInsurance
        data={dataNotInsured}
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

import React from "react";
import { vi, describe, test, expect } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { StepTop } from "../pages/wizard/StepTop";
import * as api from "../api/quoteApi";

vi.mock("../api/quoteApi", () => ({
  getQuoteByQuoteNo: vi.fn(),
}));

describe("StepTop Component (SC-001)", () => {
  const mockOnNext = vi.fn();
  const mockOnLookupSuccess = vi.fn();

  test("renders step title and guide info successfully", () => {
    render(<StepTop onNext={mockOnNext} onLookupSuccess={mockOnLookupSuccess} />);
    expect(screen.getByText("自動車保険 簡易見積")).toBeInTheDocument();
    expect(screen.getByText("車検証")).toBeInTheDocument();
    expect(screen.getByText("現在の保険証券")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "見積を始める" })).toBeInTheDocument();
  });

  test("triggers onNext callback when clicking start button", () => {
    render(<StepTop onNext={mockOnNext} onLookupSuccess={mockOnLookupSuccess} />);
    const startButton = screen.getByRole("button", { name: "見積を始める" });
    fireEvent.click(startButton);
    expect(mockOnNext).toHaveBeenCalled();
  });

  test("validates quote number search and shows error message for empty input", async () => {
    render(<StepTop onNext={mockOnNext} onLookupSuccess={mockOnLookupSuccess} />);
    const showButton = screen.getByRole("button", { name: "表示" });
    fireEvent.click(showButton);
    expect(await screen.findByText("見積番号を入力してください。")).toBeInTheDocument();
  });

  test("shows error if quote number is not found (404)", async () => {
    const errorResponse = {
      isAxiosError: true,
      response: { status: 404 }
    };
    vi.mocked(api.getQuoteByQuoteNo).mockRejectedValueOnce(errorResponse);

    render(<StepTop onNext={mockOnNext} onLookupSuccess={mockOnLookupSuccess} />);
    
    const searchInput = screen.getByPlaceholderText("見積番号を入力");
    const showButton = screen.getByRole("button", { name: "表示" });

    fireEvent.change(searchInput, { target: { value: "EST999999" } });
    fireEvent.click(showButton);

    expect(await screen.findByText("指定された見積番号は存在しません。")).toBeInTheDocument();
  });
});

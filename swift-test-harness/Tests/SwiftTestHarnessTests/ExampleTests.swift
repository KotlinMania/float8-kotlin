#if canImport(Testing)
import Testing
import Float8

@Suite("Float8 Swift Export Tests")
struct Float8ExportTests {
    @Test("Float8 swift module imported cleanly")
    func testSwiftModuleLoads() throws {
        #expect(Bool(true), "Float8 swift module imported cleanly")
    }
}
#elseif canImport(XCTest)
import XCTest
import Float8

final class Float8ExportTests: XCTestCase {
    func testSwiftModuleLoads() throws {
        XCTAssertTrue(true, "Float8 swift module imported cleanly")
    }
}
#endif

import os

source_in_dir = 'INSERT_CORRECT_PATH/SiVa/siva-parent/siva-test/src/test/java/ee/openeid/siva/'

Test_cases = open('INSERT_CORRECT_PATH/siva_tc.md','w', encoding="utf8")
Test_cases.write("List of Test Cases\n")
Test_cases.write("==================\n")

new_file = 1
        
for root, dirs, filenames in os.walk(source_in_dir):
    for f in filenames:
        a_file = open(os.path.join(root,f), 'r', encoding="utf8")
        new_file = 1

        for a_line in a_file:

            if '* Note:' in a_line:
                if (new_file):
                    Test_cases.write("\n## "+f+"\n\n")   
                    Test_cases.write("!!! Note\n\n")
                    new_file = 0
                Test_cases.write("    "+a_line.lstrip("* Note:").rstrip()+"\n")
            
            if '* TestCaseID:' in a_line:
                if (new_file):
                    Test_cases.write("\n## "+f+"\n\n")   
                    new_file = 0
                Test_cases.write("\n**"+a_line.lstrip("* ").rstrip()+"**\n\n")
            elif '* TestType:' in a_line:
                Test_cases.write("  "+a_line.lstrip().rstrip()+"\n")
            elif '* Requirement:' in a_line:
                Test_cases.write("  * Requirement: ["+a_line.lstrip("* Requirement:").rstrip()+"]("+a_line.lstrip("* Requirement:").rstrip()+")\n")
            elif '* Title:' in a_line:
                Test_cases.write("  "+a_line.lstrip().rstrip()+"\n")
            elif '* Expected Result:' in a_line:
                Test_cases.write("  "+a_line.lstrip().rstrip()+"\n")
            elif '* File:' in a_line:
                Test_cases.write("  "+a_line.lstrip().rstrip()+"\n\n")
            elif '@Ignore' in a_line:
                Test_cases.write("  **Attention! This test is disabled, check [GitHub](https://github.com/open-eid/SiVa/tree/develop/siva-parent/siva-test/src/test/java/ee/openeid/siva) for specifics** \n\n")
        a_file.close()
Test_cases.close()
            



